import dev.kord.common.entity.optional.optional
import dev.kord.core.Kord
import dev.kord.core.cache.data.EmbedData
import dev.kord.core.entity.Embed
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.EmbedBuilder

data class Question(val question : String, val multi_choice : Boolean, val owner: ULong, val ownerName: String) {
    var answers : MutableList<Answer> = mutableListOf()

    fun vote(memberId: ULong, emoji: String) {
        val answerToVote = getAnswer(emoji) ?: throw IllegalArgumentException("There is no answer with this emoji as vote emoji")
        if (!multi_choice) {
            for (answer in answers) {
                answer.removeMember(memberId)
            }
        }
        answerToVote.addMember(memberId)
    }

    fun unvote(memberId: ULong, emoji: String) {
        val answerToUnvote = getAnswer(emoji) ?: throw IllegalArgumentException("There is no answer with this emoji as vote emoji")
        answerToUnvote.removeMember(memberId)
    }

    fun isExistingVoteEmoji(emoji: String) : Boolean {
        for (answer in answers) {
            if (answer.isVoteEmoji(emoji)) return true
        }

        return false
    }

    fun getAnswer(emoji: String) : Answer? {
        for (answer in answers) {
            if (answer.isVoteEmoji(emoji)) return answer
        }

        return null
    }

    fun addAnswer(answer: Answer) {
        for (answerIn in answers) {
            if (answerIn.isVoteEmoji(answer.voteEmoji)) throw IllegalArgumentException("This Vote Emoji Already Exists")
        }

        answers.add(answer)
    }

    fun addAnswers(vararg answers: Answer) {
        for (answer in answers) {
            addAnswer(answer)
        }
    }

    fun removeAnswer(emoji: String) {
        val newAnswerList = mutableListOf<Answer>()

        for (answer in answers) {
            if (!answer.isVoteEmoji(emoji)) {
                newAnswerList.add(answer)
            }
        }

        answers = newAnswerList
    }

    fun isComplete() : Boolean {
        if (answers.size < 2) return false
        return true
    }

    fun asMessageContent() : String {
        var content = "${ownerName} Asks:\n" +
                "*${question}*\n\n"

        for (answer in answers) {
            content += "${answer.asMessageContent()}\n\n"
        }

        return content
    }

    fun asEmbedMessage() : EmbedData {
        return EmbedData(
            title = "${ownerName} pergunta:\n".optional(),
            description = "**${question}**\n\n${getAnswersSection()}".optional()
        )
    }

    fun getAnswersSection() : String {
        var section = ""

        for (answer in answers) {
            section += "${answer.asMessageContent()}\n" +
                    "${answer.getCollumString(getVoteCount(), 50)}\n\n"
        }

        return section
    }

    fun getVoteCount() : Int {
        var votes = 0
        for (answer in answers) {
            votes += answer.getNumberOfVotes()
        }
        return votes
    }
}