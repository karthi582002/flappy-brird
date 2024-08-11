import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundEffects {
    Clip clip;
    String track;
    SoundEffects(String track){
        this.track = track;
    }
    public  void playSound (){
        try {
            // Load the sound file
            File soundFile = new File(this.track); // Use the correct path

            // Create an audio input stream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            // Get a sound clip resource
            clip = AudioSystem.getClip();

            // Open the audio clip and load samples from the audio input stream
            clip.open(audioStream);

            // Start the clip
            if(this.track == "bg.wav"){
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }else {
                clip.start();

            }

            // Print to confirm that sound should be playing
            System.out.println("Sound is playing...");


        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio file.");
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.out.println("Line unavailable: " + e.getMessage());
        }
    }
    public void stopSound(){
        if(clip != null) {
            clip.stop();
        }
    }
}
