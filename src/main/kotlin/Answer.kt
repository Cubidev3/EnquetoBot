import java.lang.Math.floor
import kotlin.math.roundToInt

data class Answer(val answer : String, val voteEmoji : String) {
    private var membersId: MutableList<ULong> = mutableListOf()

    fun hasMember(memberId: ULong) : Boolean {
        for (memberIdIn in membersId) {
            if (memberIdIn == memberId) return true
        }
        return false
    }

    fun addMember(memberId : ULong) {
        if (hasMember(memberId)) return
        membersId.add(memberId)
    }

    fun removeMember(memberId: ULong) {
        val newMemberIdList = mutableListOf<ULong>()

        for (memberIdIn in membersId) {
            if (memberIdIn != memberId) {
                newMemberIdList.add(memberIdIn)
            }
        }

        membersId = newMemberIdList
    }

    fun isVoteEmoji(emoji: String) : Boolean {
        return (emoji == voteEmoji)
    }

    fun getNumberOfVotes() : Int {
        return membersId.size
    }

    fun asMessageContent(): String {
        return "${voteEmoji} - ${answer}: ${getNumberOfVotes()} Votos"
    }

    fun getCollumString(totalVotes: Int, maxSize: Int) : String {
        if (totalVotes == 0) return ">"

        var collum = ""
        val size = floor(((getNumberOfVotes() * maxSize) / totalVotes).toDouble()).roundToInt()
        for (i in 0 until size) { collum += "-" }
        collum += ">"

        return collum
    }
}