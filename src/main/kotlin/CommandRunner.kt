import com.vdurmont.emoji.EmojiManager
import com.vdurmont.emoji.EmojiParser
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji

object CommandRunner {
    data class CommandArgument(val commandMessage: Message, val survey: Survey?, val arguments: String)

    suspend fun run(commandCall: Interpreter.CommandCall) {
        if (commandCall.argument == null || commandCall.command == Interpreter.Command.None) return
        when (commandCall.command) {
            Interpreter.Command.New -> new(commandCall.argument)
            Interpreter.Command.Add -> add(commandCall.argument)
            Interpreter.Command.Remove -> remove(commandCall.argument)
            else -> return
        }
    }

    private suspend fun new(commandArgument: CommandArgument) {
        val owner = commandArgument.commandMessage.author ?: return
        val channel = commandArgument.commandMessage.channel
        val questionContent = commandArgument.arguments.ifEmpty { throw IllegalArgumentException("Question Name was not found") }

        val question = Question(questionContent, multi_choice = false, owner.id.value, owner.username)
        val survey = channel.createSurvey(question, owner)
        surveys.add(survey)
    }

    private suspend fun add(commandArgument: CommandArgument) {
        if (commandArgument.survey == null || commandArgument.arguments.length < 2) return
        if (commandArgument.survey.question.owner != (commandArgument.commandMessage.author?.id?.value ?: return)) return
        val words: MutableList<String> = commandArgument.arguments.split(' ') as MutableList<String>

        var voteEmoji = words.last()
        if (!EmojiManager.isEmoji(voteEmoji)) return
        voteEmoji = EmojiParser.parseToUnicode(voteEmoji)
        words.removeAt(words.lastIndex)

        println(commandArgument.arguments)
        val answerContent = Interpreter.getArguments(words)
        println(answerContent)

        val answer = Answer(answerContent, voteEmoji)
        commandArgument.survey.question.addAnswer(answer)
        commandArgument.survey.update()

        commandArgument.survey.message.addReaction(ReactionEmoji.Unicode(voteEmoji))
        /* val answerContent = commandArgument.arguments[0]
        val voteEmoji = commandArgument.arguments[1]
        val answer = Answer(answerContent, voteEmoji)

        commandArgument.survey.question.addAnswer(answer)
        commandArgument.survey.update()

        commandArgument.survey.message.addReaction(ReactionEmoji.Unicode(voteEmoji)) */
    }

    private suspend fun remove(commandArgument: CommandArgument) {
        if (commandArgument.survey == null || commandArgument.arguments.isEmpty()) return
        if (commandArgument.survey.question.owner != (commandArgument.commandMessage.author?.id?.value ?: return)) return

        var voteEmoji = commandArgument.arguments
        if (!EmojiManager.isEmoji(voteEmoji)) return
        voteEmoji = EmojiParser.parseToUnicode(voteEmoji)

        commandArgument.survey.question.removeAnswer(voteEmoji)
        commandArgument.survey.update()

        val unicodeEmoji = ReactionEmoji.Unicode(voteEmoji)

        commandArgument.survey.message.deleteReaction(unicodeEmoji)
    }
}