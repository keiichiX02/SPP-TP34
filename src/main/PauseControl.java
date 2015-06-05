package main;

public class PauseControl {
    private boolean needToPause;

    
    public PauseControl() {
		this.needToPause = true;
	}

	public synchronized void pausePoint() {
        while (needToPause) {
            try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//System.out.println("Thread interrupted");
				//e.printStackTrace();
			}
        }
    }

    public synchronized void pause() {
        needToPause = true;
    }

    public synchronized void unpause() {
        needToPause = false;
        this.notifyAll();
    }
}