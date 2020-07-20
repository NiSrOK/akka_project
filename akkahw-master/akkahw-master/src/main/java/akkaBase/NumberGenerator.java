package akkaBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import akka.actor.typed.ActorRef;
// пакет akka.actor.typed содержит классы ядра akka (это база)
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akkaBase.HelloWorld.ChangeMessage;
import akkaBase.StatCalculator.Command;

/*
	Создаем класс NumberGenerator, объекты которого обладают способностью
	а) принимать dt - время до генерации следующего числа
	б) генерировать числа через dt и помещать их в свой массив numArray
	
	Класс наследуется от AbstractBehavior<T>, который типизирован NumberGenerator.Command 

    Каждый субъект определяет тип T для сообщений, которые он может получать. 
    Сообщения являются неизменяемыми, поддерживают сопоставление с образцом.
    
    При определении актеров и их сообщений придерживаемся рекомендаций:
    1. Поскольку сообщения являются общедоступным API-интерфейсом Actor, 
       рекомендуется качественно именовать сообщения (чтобы смысл их был ясен),
       даже если они просто "переносят тип данных". 
       Это облегчит использование, понимание и отладку.

    2. Сообщения должны быть неизменными, так как они разделены между различными потоками.

    3. Хорошей практикой является размещение связанных с актером сообщений в виде статических классов 
       в классе AbstractBehaavior. Это облегчает понимание того, какие сообщения ожидает 
       и обрабатывает субъект.

    4. Хорошей практикой является получение исходного поведения актера с помощью статического метода фабрики.
*/

public class NumberGenerator extends AbstractBehavior<NumberGenerator.Command> {

	private ActorRef<StatCalculator.Command> replyTo;
	public static int load_priority = 1;
	private List<Integer> numArray = new ArrayList<Integer>();
	private long dt = 1000;
	private int startNum = 0;
	private int countEnd = 10;
	
	/* *************** Определяем команды (начало) *************************** 
	   Интерфейс                                                                                   */
	interface Command{}
	
	//...........   Реализация интерфейса command ..............
	public enum NumGenCommand implements Command{
		HELLO,
		START_GEN
	}
	
	public static class NumbGen implements Command {
		public final ActorRef<StatCalculator.Command> replyTo;

		public NumbGen(ActorRef<StatCalculator.Command> replyTo) {
			super();
			this.replyTo = replyTo;
		}
	}
	
	
	// ***************команды (конец) ***************************  
   
	// Конструктор 
	private NumberGenerator(ActorContext<Command> context) {
		super(context);
	}
	
	// static фабричный метод, 
	public static Behavior<Command> create(){
		return Behaviors.setup(context -> new NumberGenerator(context));
	}
	
		@Override
	public Receive<Command> createReceive(){
		return newReceiveBuilder()
			   .onMessageEquals(NumGenCommand.HELLO,this::onSayHello)
			   .onMessage(NumbGen.class, this::onStartGen)
			   .build();
	}
		
	// Обработчики событий передачи сообщения
	private Behavior<Command> onSayHello(){
		getContext().getLog().info("Hello, I am NumberGenerator");
		return this;
	}
	
	private Behavior<Command> onStartGen(NumbGen command){
		replyTo = command.replyTo;
		int i=0;
		while (countEnd-->0) {
			try {
				Thread.sleep(dt);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int a = 1;
			int b  = 3;
			int diff = b - a;
			Random random = new Random();
			int load_priority = random.nextInt(diff + 1);
			load_priority += a;
			numArray.add(load_priority);
			getContext().getLog().info("gen new priority: {}", numArray.get(i));
			i++;
			replyTo.tell(new StatCalculator.CalcStat(numArray));
			replyTo.tell(StatCalculator.StatCalcCommand.START_CALC);
		}
		return this;
	}
	
	
		
}