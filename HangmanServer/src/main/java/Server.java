import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.Consumer;

import static jdk.javadoc.internal.doclets.toolkit.util.Utils.toLowerCase;


public class Server{

	int count = 1;
	int portNumber;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	int guesses;




	Server(int port, Consumer<Serializable> call){
		portNumber = port;
		callback = call;
		server = new TheServer();
		server.start();
	}





	public class TheServer extends Thread{

		public void run() {
			try(ServerSocket mysocket = new ServerSocket(portNumber);){
		    System.out.println("Server is waiting for a player!");
			while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("player has connected to server: " + "client #" + count);
				clients.add(c);
				c.start();
				
				count++;
				
			    }
			}catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}
		}

		 class ClientThread extends Thread {


			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			public String currentWord;
			public String category;
			int guesses = 1;
			int guessAttempts = 0;

			public List<String> categories = Arrays.asList("animals", "cars", "names");
			public HashSet<String> chosenCategories = new HashSet<>();
			public HashSet<String> wonCategories = new HashSet<>();

			List<String> animalWords = Arrays.asList("elephant", "lion", "giraffe", "monkey", "tiger",
					"cat","dog","penguin","snake","skunk",
					"eagle","parrot","jaguar","hyena");
			List<String> carWords = Arrays.asList("tesla", "toyota", "bmw", "ford", "honda",
					"audi","mercedes","jeep","hyundai","porsche","ram",
					"dodge","gmc","cadillac","chevy","kia","fiat","jaguar");
			List<String> nameWords = Arrays.asList("alice", "ali","bob", "charlie", "david",
					"emily","sami","jamil","martin","john","joseph",
					"josh","carl","michael","nathan","christian","brian");

			ClientThread(Socket socket, int count) {
				this.connection = socket;
				this.count = count;
			}

			public  ArrayList<Integer> findIndices(String str, char letter) {
				ArrayList<Integer> indices = new ArrayList<>();

				for (int i = 0; i < str.length(); i++) {
					if (str.charAt(i) == letter) {
						indices.add(i+1);
					}
				}

				return indices;
			}

			public String getCurrentWord(){
				return currentWord;
			}
			public String getCategory(){
				return category;
			}

			public void setCurrentWord(String word){
				this.currentWord = word;
			}

			public void setCategory(String category){
				this.category = category;
			}


			public void updateClient(String message) {

					ClientThread t = this;
					try {
						t.out.writeObject(message);
					} catch (Exception e) {}
			 }


			public synchronized void run() {

				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);
				} catch (Exception e) {
					System.out.println("Streams not open");
				}


				updateClient("new player on server: player #" + count);


				// process the players guesses
				while (true) {
					try {

						String clientInput = in.readObject().toString();

						if(clientInput.charAt(0) == 'Q') {
							callback.accept("player# " + count + " quit the game.");
							updateClient("YOU QUIT BETTER LUCK NEXT TIME!");
							clients.remove(this);
							break;
						}
						if(clientInput.charAt(0) == 'A' && wonCategories.size() != 3){

							updateClient("You can not restart yet!");
						}
						if(clientInput.charAt(0) == 'A' && wonCategories.size() == 3){
							guesses = 1;
							wonCategories.clear();
							currentWord = "";
							category = "";
							callback.accept("player# " + count + "wants to play again.");
							updateClient("You want to play again? choose a category.");
							clientInput = in.readObject().toString();
						}
						if(clientInput.charAt(0) == 'G'){
							String guess = clientInput.substring(2);
							if(guess.equals(currentWord)) {
								guesses = 1;
								callback.accept("player# " + count + " guessed " + category + " correctly.");
								updateClient("WOW! you guessed the right word! choose another category.");
								updateClient(category + " can no longer be played.");
								wonCategories.add(category);
								if(wonCategories.size() == 3){
									wonCategories.clear();
									chosenCategories.clear();
									callback.accept("player# " + count + " won their game.");
									updateClient("YOU WON!! GOOD JOB!");
									updateClient("enter A to play again or Q to quit.");
									clientInput = in.readObject().toString();
									continue;
								}
								updateClient("request a new category");
								guessAttempts = 0;
								clientInput = in.readObject().toString();
								setCategory("");
								setCurrentWord("");
							}else{
								guessAttempts++;
								int guessesRemaining = 3 - guessAttempts;
								callback.accept("player# "+ count+" incorrectly guessed the word.");
								updateClient("WRONG! try again.");
								updateClient("You have "+ guessesRemaining + " guess attempts left.");
							}
						}
						if(guesses >= 6){
							callback.accept("player# "+ count + " could not guess the word correctly in " + category + ".");
							updateClient("too many guesses you lost in this category.");
							updateClient(category+ " can no longer be played.");
							chosenCategories.remove(category);
							setCategory("");
							setCurrentWord("");
							updateClient("request a new category.");
							guessAttempts = 0;
							clientInput = in.readObject().toString();
							guesses = 1;
						}

						if(chosenCategories.contains(clientInput)) {
							updateClient("this category has been chosen already try again.");
						} else if(categories.contains(clientInput)){
							setCategory(clientInput);
							updateClient("category chosen: "+ category);
							callback.accept("player# " + count +" chose " + category);
							Random random = new Random();
							if(Objects.equals(category, "animals")){
								int randomIndex = random.nextInt(animalWords.size());
								String word = animalWords.get(randomIndex);
								setCurrentWord(word);
								updateClient("the word is " + word.length() + " letters long.");
								chosenCategories.add(category);
							}else if(Objects.equals(category, "cars")){
								int randomIndex = random.nextInt(carWords.size());
								String word = carWords.get(randomIndex);
								setCurrentWord(word);
								updateClient("the word is " + word.length() + " letters long.");
								chosenCategories.add(category);
							}else if(Objects.equals(category, "names")){
								int randomIndex = random.nextInt(nameWords.size());
								String word = nameWords.get(randomIndex);
								setCurrentWord(word);
								updateClient("the word is " + word.length() + " letters long.");
								chosenCategories.add(category);
							}
							continue;
						}else if(!categories.contains(clientInput) && Objects.equals(category, "") && !Objects.equals(clientInput, "")){
							updateClient("category does not exist try again.");
						}

						if(Objects.equals(currentWord, "") && !Objects.equals(category,"")){
							updateClient("category has not been chosen yet.");
						}else if (category != ""){
							ArrayList<Integer> indices = findIndices(currentWord,clientInput.charAt(0));
							if(indices.isEmpty() && clientInput.charAt(0) != 'G'){
								int guessesRemaining = 6 - guesses;
								updateClient(clientInput.charAt(0)+" does not exist in the word you have " + guessesRemaining + " guesses left.");
								guesses++;
							} else if (clientInput.charAt(0) != 'G'){
								updateClient("letter "+ clientInput.charAt(0)+" produced these indices: "+String.valueOf(indices));
							}
						}
						if(wonCategories.size() == 3){
							callback.accept("player# " + count + " won their game.");
							updateClient("YOU WON!! GOOD JOB!");
							updateClient("enter A to play again or Q to quit.");
							clientInput = in.readObject().toString();
						}

					} catch (Exception e) {}

				}//end of run


			}//end of client thread
		}
}


	
	

	
