import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Scanner;

public class Main {
	public final static Logger logger = LoggerFactory.getLogger(Main.class);
	public static char prefix = ',';
	public static long StartTime;

	public static void main(String[] args){
		try {bot();}
		catch (Exception e){e.printStackTrace();}
	}

	public static void bot() throws InterruptedException{
		// Initialize commands
		new Echo();
		new EchoMember();
		new EchoChannel();
		new EchoRole();
		new EchoAttachment();
		new Ping();
		new Help();
		logger.info("Initialized commands");
		System.out.println();

		// Initialize bot
		String token;
		{
			token = "--------------------TOKEN GOES HERE-------------------------";}

		JDA api = JDABuilder.createDefault(token)
				.enableIntents(GatewayIntent.DIRECT_MESSAGES)
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setEventPassthrough(true)
				.addEventListeners(new CommandListener()) // see command listener class for command handler stuff
				.setActivity(Activity.watching("Prefix " + prefix))
				.build();

		api.awaitReady();
		logger.info("Bot online");

		StartTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

		Scanner scanner = new Scanner(System.in);
		meow: while (true) {
			try {
				System.out.print("> ");
				String[] input = scanner.nextLine().split(" ");
				switch (input[0].toLowerCase()) {
					case "countusers":
						for (User user : api.getUsers())
							System.out.println(user.getName());
					break;
					case "kill", "exit", "explode", "stop":
						api.shutdownNow();
						break meow;
					case "uptime":
						logger.info("uptime: " + (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - StartTime) + " seconds");
						break;
					case "fetchusers":
						if (input.length < 2) System.out.println("cant do that, you forgot the server id");
						else api.getGuildById(input[1]).loadMembers();
						break;
					default:
						logger.info("Command not found " + input);
				}
			} catch (Exception e) {e.printStackTrace();}
		}

		api.awaitShutdown();
		logger.info("exploded");
	}
}