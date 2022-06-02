import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Embed
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.modify.embed

data class Survey(val message: Message, val question: Question) {
    suspend fun update() {
        val updatedEmbed = question.asEmbedMessage()
        val oldEmbed = message.embeds.firstOrNull()
        message.edit {
            embed {
                author {
                    this.name = oldEmbed?.author?.name
                    this.icon = oldEmbed?.author?.iconUrl
                }
                this.title = updatedEmbed.title.value
                this.description = updatedEmbed.description.value
                this.color = oldEmbed?.color
            }
        }
    }
}

suspend fun MessageChannelBehavior.createSurvey(question: Question, author: User?) : Survey {
    val embed = question.asEmbedMessage()

    val message = this.createEmbed {
        author {
            name = author?.username
            icon = author?.avatar?.url
        }
        title = embed.title.value
        description = embed.description.value
        color = author?.accentColor
    }

    for (answer in question.answers) {
        message.addReaction(ReactionEmoji.Unicode(answer.voteEmoji))
    }

    return Survey(message, question)
}