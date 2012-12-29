package code.snippet
import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import code.comet.PokerServer
import code.model._
import util.Helpers._
import scala.xml.NodeSeq
import net.liftweb.common.Logger


object UserStore {
  object userName extends SessionVar[String]("");
  def leave:Unit  = {
    PokerServer ! RemoveUser(userName.get)
    userName.remove
    }
  def save(name:String):Unit = {
    
  }
}

class PokerControl extends Logger {
  def saveUser(s:String) = {
    if(!s.isEmpty()) {
    	UserStore.userName.set(s)
    	PokerServer ! AddUser(s)
    }
    RedirectTo("vote.html")  
  }

  def leave = { 
	UserStore.leave
    RedirectTo("vote.html")
  }

  def user = ".login_user_name" #> UserStore.userName.get
  
  def vote(v:String):Unit = {
   info("val rec "+v)
   val vv = v match {
     case "?" => -1
     case "∞" => 1000
     case x => x.toInt
   }
   PokerServer ! Vote(UserStore.userName.get,vv) 
  }
  
  def render = { 
	  if(UserStore.userName.get.isEmpty()) {
		  ".with-name-only" #> "" &
		  "name=username" #> SHtml.onSubmit( s => saveUser(s.trim())  ) 
	  } else {
	      ".username" #> "" &
	      "name=show [onclick]" #> SHtml.ajaxInvoke(() =>  (PokerServer ! ShowAll) ) &
	      "name=new_vote [onclick]" #> SHtml.ajaxInvoke( () => (PokerServer ! NewVoting )) &
	      "name=leave [onclick]" #> SHtml.ajaxInvoke( () => leave ) &
	      ".actbuttons input [onclick]" #> SHtml.ajaxCall( JsRaw("$(this).val()") , vote ) 
 	  }
  }

}