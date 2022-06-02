import dev.kord.core.entity.Message

object Interpreter {
    enum class Command {
        New,
        Add,
        Remove,
        End,
        None
    }

    data class CommandCall(val command: Command, val argument: CommandRunner.CommandArgument?)

    fun analize(message: Message) : Command {
        val words = message.content.split(' ')

        val firstWord = words.firstOrNull()?.lowercase()
        if (firstWord != "q" && firstWord != "enquete") return Command.None
        val commandSignature = words.elementAtOrNull(1)?.lowercase()

        return when (commandSignature) {
            null -> Command.None
            "new" -> Command.New
            "novo" -> Command.New
            "nova" -> Command.New
            "n" -> Command.New

            "add" -> Command.Add
            "adicionar" -> Command.Add
            "a" -> Command.Add

            "remove" -> Command.Remove
            "remover" -> Command.Remove
            "r" -> Command.Remove

            "end" -> Command.End
            else -> Command.None
        }
    }

    fun getCommandCall(message: Message) : CommandCall {
        val command = analize(message)
        val args = getArguments(message.content)

        return when (command) {
            Command.New -> CommandCall(
                command,
                CommandRunner.CommandArgument(message, null, args),
            )
            Command.Add -> CommandCall(
                command,
                CommandRunner.CommandArgument(message, getSurvey(message, surveys), args)
            )
            Command.Remove -> CommandCall(
                command,
                CommandRunner.CommandArgument(message, getSurvey(message, surveys), args)
            )
            else -> CommandCall(Command.None, null)
        }
    }

    fun getArguments(message: String) : String {
        val words = message.split(' ')

        if (words.size < 3) return ""

        println(words)
        var str = ""

        for (wordIdx in 2..words.lastIndex) {
            val word = words[wordIdx]
            str += "$word "
        }

        return str.trim(' ')
    }

    fun getArguments(words: List<String>) : String {
        if (words.isEmpty()) return ""
        if (words.size == 1) return words.first()

        println(words)
        var str = ""

        for (wordIdx in 0..words.lastIndex) {
            val word = words[wordIdx]
            str += "$word "
        }

        return str.trim(' ')
    }

    fun getSurvey(message: Message, surveyList: List<Survey>) : Survey? {
        if (message.referencedMessage == null) return null
        for (survey in surveyList) {
            if (survey.message == message.referencedMessage) {
                return survey
            }
        }
        return null
    }
}