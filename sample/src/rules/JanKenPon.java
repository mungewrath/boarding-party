package rules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import network.NetworkAdapter;
import network.NetworkPlayerNotifier;
import presenter.IPlayerNotifier;
import core.GameState;
import core.GameStateException;
import core.Player;

public class JanKenPon {
	static boolean isMaster = true;
	static final int portNumber = 7;
	
	public static Player chooseOpponent(String opponentTypeArg, String remotePlayerNameArg, String remoteAddress, Scanner input) throws Exception {
		int opponentType = obtainOpponentType(opponentTypeArg, input);
		if(opponentType == 1) {
			JanKenPonAI ai = new JanKenPonAI();
			return new Player("Al the AI", ai);
		} else if(opponentType == 2) {
			System.out.println("Hosting game. Your local IP address is " + Inet4Address.getLocalHost().getHostAddress());
			try {
	            ServerSocket serverSocket =
	                new ServerSocket(portNumber);
	            Socket clientSocket = serverSocket.accept();
	            serverSocket.close();
				System.out.println("Connection accepted.");
				return createNetworkPlayer(input, clientSocket, remotePlayerNameArg);
	        } catch (IOException e) {
	            System.out.println("Exception caught when trying to listen on port "
	                + portNumber + " or listening for a connection");
	            System.out.println(e.getMessage());
	            throw e;
	        }
		} else {
			isMaster = false;
			String hostName = obtainStringInput(remoteAddress, input, "Enter hostname to connect to:");
			Socket socket = new Socket(hostName, portNumber);
			System.out.println("Connected to server.");
			return createNetworkPlayer(input, socket, remotePlayerNameArg);
		}
	}
	
	private static Player createNetworkPlayer(Scanner input, Socket socket, String remotePlayerNameArg) throws Exception {
		String playerName = obtainStringInput(remotePlayerNameArg, input, "Enter remote player's name:");
		NetworkAdapter adapter = new NetworkAdapter(socket);
		IPlayerNotifier notifier = new NetworkPlayerNotifier(adapter, playerName);
		adapter.beginListening();
		return new Player(playerName, notifier);
	}
	
	private static String getCommandLineArg(int n, String[] args) {
		if(args.length <= n) {
			System.out.println("No parameter passed in for arg " + n);
		}
		return (args.length > n ? args[n] : null);
	}
	
	private static String obtainStringInput(String cmdLineName, Scanner input, String prompt) {
		if(cmdLineName != null) {
			return cmdLineName;
		} else {
			System.out.println(prompt);
			return input.nextLine();
		}
	}
	
	private static int obtainOpponentType(String cmdLineType, Scanner input) {
		final int maxChoice = 3;
		final String errorMessage = "Invalid opponent choice.";
		int choice = -1;
		
		if(cmdLineType != null) {
			choice = parseInt(cmdLineType, errorMessage);
		}
				
		while(choice > maxChoice || choice <= 0) {
			System.out.println("Choose opponent:");
			System.out.println("1 = AI");
			System.out.println("2 = Host Network");
			System.out.println("3 = Join Network");
			
			String next = input.nextLine();
			choice = parseInt(next, errorMessage);
		}
		
		return choice;
	}
	
	private static int parseInt(String arg, String errorMessage) {
		try {
			return Integer.valueOf(arg);
		} catch(NumberFormatException e) {
			System.out.println(errorMessage);
			return -1;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Scanner input = new Scanner(System.in);
		
		String playerNameArg = getCommandLineArg(0, args);
		String opponentTypeArg = getCommandLineArg(1, args);
		String remotePlayerNameArg = getCommandLineArg(2, args);
		String remoteAddress = getCommandLineArg(3, args);
		
		String name = obtainStringInput(playerNameArg, input, "What is your name?");
		
		List<Player> players = new ArrayList<Player>();
		
		JanKenPonTextPresenter presenter = new JanKenPonTextPresenter();
		Player humanPlayer = new Player(name, presenter);
		Player otherPlayer = null;
		
		try {
			otherPlayer = chooseOpponent(opponentTypeArg, remotePlayerNameArg, remoteAddress, input);
		} catch(Exception e) {
			System.out.println("Error setting up opponent: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		players.add(humanPlayer);
		players.add(otherPlayer);
		
		IGameRules rules = new JanKenPonGameRules();
		GameState game;
		try {
			game = new GameState(rules, players, isMaster);
			presenter.state = game;

			try {
				rules.initialize(game);
			} catch(RulesException e) {
				throw new GameStateException(e.getMessage());
			}
		} catch(GameStateException e) {
			System.out.println("Error running game: "+e.getMessage());
		}
		while(!rules.isGameOver()) {
			
			Thread.sleep(1000);
		}
	}

}
