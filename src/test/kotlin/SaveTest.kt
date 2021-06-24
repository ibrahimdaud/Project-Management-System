import com.COMP1815.kotlin.data.Loader
import com.COMP1815.kotlin.model.Project
import com.COMP1815.kotlin.model.Task
import com.COMP1815.kotlin.model.Team
import com.COMP1815.kotlin.model.User

import com.COMP1815.kotlin.data.FORMAT as F

fun main() {
    val l = Loader()
    val ibr = User("ibrahimdaud@gmail.com", "abc")
    val joy = User("junayidchowdhury@gmail.com", "efg")
    val ayu = User("ayubmohamed@gmail.com", "nil")
    val team = Team("Team Test", ibr, mutableListOf(ibr, joy, ayu))
    val p = Project(name = "test", admin = ibr, startDate = F.parse("08/11/2020"))
    Task(
            "Task A",
            "A simple test",
            10,
            team,
            p
    )
    println(ibr.toString())
    println(joy.toString())
    println(ayu.toString())
    println(team.toString())
    println(p.toString())
    l.save()
}