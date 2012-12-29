package code.comet
import net.liftweb._
import http._
import actor._
import code.model._
import net.liftweb.common.Logger


sealed abstract class Voting

case class VotingProgress(
    val votes: Map[String,Option[Int]]) extends Voting

case class VotingComplete (
    val votes: Map[String,Option[Int]], // votes
    val minv: Int,
    val maxv: Int, //names of voters
    val finalResult: Int //agreed result
    ) extends Voting {
  def isExtreme(v:Option[Int]):String = {
    v match {
      case Some(x :Int) => 
        if(finalResult<0) {
        	if(x == minv) "minvote" 
        	else if(x==maxv) "maxvote" 
        	else ""
        } else ""
      case None => ""
    }
  }
}

object PokerServer extends LiftActor with ListenerManager with Logger {
  val pokerValues = List(1,2,3,5,8,13,20,40,100)
  
  def forceValue(x:Option[Int]):Option[Int] = x match {
    case None => Some(-1)
    case Some(x) => Some(x)
    }
  
  def clearState() = users = users.map(f => f._1 -> None )
  def showAll() =  users = users.map (f => f._1 -> forceValue(f._2) ) 
  
  // message to send to clients 
  def createUpdate():Voting = {
   	 val complete =  !users.values.isEmpty && ! users.values.exists( _==None)
   	 if(complete) {
	   	 val values = users.values.flatten
	   	 val minVal = values.min
	   	 val maxVal = values.max
	   	 val minI=pokerValues.indexOf(minVal)
	   	 val maxI=pokerValues.indexOf(maxVal)
	   	 val finalResult= if (minI>=0 && maxI>=0 && maxI-minI<=1) maxVal
	   	 else -1;
	   	 VotingComplete(users,minVal,maxVal,finalResult)
   	 } else {
   	     VotingProgress(users)
   	 }
   	 
   	 
  }
  
  //user -> vote
  private var users:Map[String,Option[Int]] =   Map()
 
  
  override def lowPriority = {
    case nu:AddUser => users += (nu.user -> None) ; info(nu); updateListeners() 
    case v:Vote => users = users.updated(v.user,Some(v.vote)) ; info(v);  updateListeners()
    case NewVoting => clearState() ; info("new voting") ; updateListeners()
    case ShowAll => showAll ; info("showAll"); updateListeners()
    case ru:RemoveUser => users -= ru.user ; info(ru) ;updateListeners()
  }
  
}