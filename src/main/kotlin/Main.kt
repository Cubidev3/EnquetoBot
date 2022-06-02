
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.event.message.ReactionRemoveEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent

    val BOT_TOKEN: String = "Place your bot token here"

    val surveys = mutableListOf<Survey>()

    @OptIn(PrivilegedIntent::class)
    suspend fun main() {
        val kord = Kord(BOT_TOKEN)
        kord.on<MessageCreateEvent> {
            val user = message.author
            if (user != null) {
                val command = Interpreter.getCommandCall(message)
                CommandRunner.run(command)
                if (command.command != Interpreter.Command.None) {
                    message.delete("Enqueto Command")
                }
            }
        }

        kord.on<ReactionAddEvent> {
            for (survey in surveys) {
                if (survey.message == message && !getUser().isBot && survey.question.isExistingVoteEmoji(emoji.name)) {
                    survey.question.vote(userId.value, emoji.name)
                    survey.update()
                }
            }
        }

        kord.on<ReactionRemoveEvent> {
            for (survey in surveys) {
                if (survey.message == message && !getUser().isBot && survey.question.isExistingVoteEmoji(emoji.name)) {
                    survey.question.unvote(userId.value, emoji.name)
                    survey.update()
                }
            }
        }

        kord.login {
            intents += Intent.MessageContent
            intents += Intent.GuildMessageReactions
            intents += Intent.GuildMessages
        }
    }