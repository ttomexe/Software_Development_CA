public class InterruptApp {
    public static void main(String args []){
        Thread a = new Thread (new Interrupt());
        Thread b = new Thread (new Interrupt());
        a.start();
        b.start();
        System.out.println("Main thread still running");

        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){}
        a.interrupt();
        b.interrupt();
    }
}
