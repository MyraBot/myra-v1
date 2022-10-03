# ✨ Myra v1

With this version Myra went from a private bot to a public one. I also switched from gradle to maven. This is also the reason
for an entire new repository. Because this was my first ever project you will find some very dirty code. I advise you to not
copy code from here. Use the code snippets to get inspiration on how to do things, but not to just copy and paste in your
project. Also, this Bot uses a very old Discord API version and probably won't work well with the new changes. Hope you can
find some helpful things here!

![Warning](https://raw.githubusercontent.com/MyraBot/.github/main/code-advise.png)

## 📌 Table of content

* [Features](#-features)
* [Dependencies](#-dependencies)

## 📚 Features

* Miscellaneous
  * Media notifications
    * Twitch notifications
    * YouTube notifications
  * Reaction roles
    * Normal mode (unlimited roles from a message)
    * Verify mode (once you got the role you can't lose it)
    * Unique mode (only get one role from a message)
  * Global chat
  * Change prefix
  * Say command
  * Toggle commands on or off
  * Suggestions
  * @someone
* Economy
  * Balance command
  * Balance leaderboard
  * Buy roles
  * Daily command
  * Fishing
  * Give command
  * Streak command
  * Blackjack
  * Change currency
  * Set members money
* Fun
  * Meme command
  * Text formatter
  * Would you rather command
* General
  * Information commands
    * Bot
    * Server
    * User
  * Avatar command
  * Calculator
  * Reminders
* Help
  * Commands overview
  * Feature suggestion
  * Help command
  * Invite command
  * Invite thanks message
  * Ping command
  * Bug report command
  * Support link command
  * Bot site voting command
* Leveling
  * voice leveling
  * message leveling
  * Change background of leveling card
  * Rank command
  * Voice call time command
  * Voice call time leaderboard
  * Custom level up message channel
  * Set a members level
  * Leveling roles
  * Leaderboard command
  * Xp leaderboard
* Moderation
  * Banning
    * Ban command
    * Temporary ban command
    * Unban command
  * Muting
    * Mute command
    * Change mute role
    * Temporary mute command
    * Unmute command
  * Clear command
  * Kick command
  * Change nickname command
* Music
  * Clear queue command
  * Live music controller
  * Track information command
  * Join command
  * Leave command
  * Play command
  * Queue overview command
  * Repeat song command
  * Shuffle queue command
  * Skip song command
  * Stop music
  * Voting for music commands
* Welcoming
  * Automatic role
  * Direct messages
  * Embeds
  * Images
    * Custom backgrounds
    * Different fonts

#### Bot owner only

* Dashboard command
* Get invite by server id
* Custom embeds for my own discord
* Special roles
  * Unicorn role (shhhh!)
  * Reward role for server owners with Myra
* Server tracking
* Set premium
* Shutdown

## 📌 Dependencies

* [JDA](https://github.com/DV8FromTheWorld/JDA) Discord API wrapper
* [JDA Utilities](https://github.com/JDA-Applications/JDA-Utilities) Event waiter
* [mongodb](https://mvnrepository.com/artifact/org.mongodb/mongodb-driver) Mongodb driver
* [Lavaplayer](https://github.com/sedmelluq/lavaplayer) Encoding opus audio
* [Logback classic](https://mvnrepository.com/artifact/ch.qos.logback/logback-classic) Logging