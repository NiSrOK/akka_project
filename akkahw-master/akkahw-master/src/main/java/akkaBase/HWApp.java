package akkaBase;

import akka.actor.typed.ActorSystem;

public class HWApp {
	public static void main(String[] args) {
		ActorSystem<HelloWorld.Command> mySystem = 
				ActorSystem.create(HelloWorld.create(), "MySystem");
		mySystem.tell(HelloWorld.HelloCommand.HELLO);
		mySystem.tell(new HelloWorld.ChangeMessage("message was sended before command START_GEN"));
		mySystem.tell(HelloWorld.HelloCommand.HELLO);
		mySystem.tell(HelloWorld.HelloCommand.START_GEN);
		//mySystem.tell(HelloWorld.HelloCommand.START_CALC);
		mySystem.tell(new HelloWorld.ChangeMessage("message was sended after command START_GEN"));
		mySystem.tell(HelloWorld.HelloCommand.HELLO);
		try {
			Thread.sleep(11000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mySystem.terminate();
	}

}
