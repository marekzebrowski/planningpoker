package code.comet
import net.liftweb._
import http._
import actor._
import code.model._
import net.liftweb.common.Logger

object PokerServer extends LiftActor with ListenerManager with Logger {

  def forceValue(x:Option[Int]):Option[Int] = x match {
    case None => Some(-1)
    case Some(x) => Some(x)
    }
  
  def clearState() = users = users.map(f => f._1 -> None )
  def showAll() =  users = users.map (f => f._1 -> forceValue(f._2) ) 
  
  // message to send to clients 
  def createUpdate = users
  
  private var users:Map[String,Option[Int]] =   Map()
  
  override def lowPriority = {
    case nu:AddUser => users += (nu.user -> None) ; info(nu); updateListeners() 
    case v:Vote => users = users.updated(v.user,Some(v.vote)) ; info(v);  updateListeners()
    case NewVoting => clearState() ; info("new voting") ; updateListeners()
    case ShowAll => showAll ; info("showAll"); updateListeners()
    case ru:RemoveUser => users -= ru.user ; info(ru) ;updateListeners()
  }
  
}