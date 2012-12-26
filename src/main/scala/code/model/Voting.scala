package code.model

/*
 * messages sent to actors 
 */
case class Vote(val user:String, val vote:Int)
case object NewVoting
case object ShowAll
case class AddUser(val user:String)
case class RemoveUser(val user:String)

