# Hai hai ! :3
Welcome to my jda framework thing!

This utilizes reflection and there's a [reflectionless version](https://github.com/computer-catt/JDA-Framework/tree/Reflectionless) available with less features

This was a thing made for JDA v5 because I wanted to make a small discord bot, contributions are welcome!

Before using you should probably have a base knowledge of [jda](https://jda.wiki/introduction/jda/).

Attribution isn't required (as implied by the mit license) but it would be cool :3

### Features

this framework includes stuff like
- easy command registration
- automatic variable parsing for command inputs(see sample commands)
- command aliases
- slash commands by default
- terminal commands(expandable)
  - count users
  - kill commands
  - uptime
  - fetchusers
- sample commands
  - echo attachment
  - echo channel
  - echo member
  - echo role
  - echo
  - ping

### Key parts

the most important stuff in this codebase are the:
- CommandBase - the class you extend to make a new command
- Main - where you set up jda and initialize commands
- CommandListener - the thing that parses and interprets events
- CommandArgument - the annotation for automatic variables

### Thank you!

Feel free to make an issue or submit a pull request for fixes