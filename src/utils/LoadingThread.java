package utils;

public class LoadingThread extends Thread {
    
    private String text;

    public LoadingThread(String text) {
        this.text = text;
    }

    @Override
    public void run() {
        System.out.print(text);
        
        try {
            for (int i = 0; i < 4; i++) {
                Thread.sleep(500); 
                System.out.print("."); 
            }
            System.out.println(" [OK]"); 
            
        } catch (InterruptedException e) {
            System.out.println("Erro ao carregar a");
            e.printStackTrace();
        }
    }
}
