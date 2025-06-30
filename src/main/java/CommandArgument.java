import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// can annotate
/// String
/// Integer / int
/// Boolean
/// User
/// GuildChannel
/// Role
/// Long / long
/// Double / double
/// Message.Attachment

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CommandArgument {
	String description();
	OptionType OPTION_TYPE() default OptionType.UNKNOWN;
	boolean required() default true;
}