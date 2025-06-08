import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Scanner;

public class Main {
	public final static Logger logger = LoggerFactory.getLogger(Main.class);
	public static char prefix = '!';
	public static long StartTime;

	public static void main(String[] args){
		try {bot();}
		catch (Exception e){e.printStackTrace();}
	}

	public static void bot() throws InterruptedException {
		// Initialize commands
		new Help();
		new Echo();
		new Ping();

		// Initialize bot
		String token;
		{
			token = "--------------------TOKEN GOES HERE-------------------------";}

		JDA api = JDABuilder.createDefault(token)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.setEventPassthrough(true)
				.addEventListeners(new CommandListener()) // see command listener class for command handler stuff
				.setActivity(Activity.watching("Prefix " + prefix))
				.build();

		api.awaitReady();
		logger.info("Bot online");

		StartTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

		Scanner scanner = new Scanner(System.in);
		meow: while (true){
			String input = scanner.nextLine();
			switch (input.toLowerCase()){
				case "kill", "exit", "explode", "stop":
					api.shutdownNow();
					break meow;
				case "uptime":
					logger.info("uptime: " + (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - StartTime) + " seconds");
					break;
			}
		}

		api.awaitShutdown();
		logger.info("exploded");
	}
}