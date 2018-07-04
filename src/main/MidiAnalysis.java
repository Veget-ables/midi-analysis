package main;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @see Sequence    "https://docs.oracle.com/javase/jp/6/api/javax/sound/midi/Sequence.html"
 * @see Track       "https://docs.oracle.com/javase/jp/6/api/javax/sound/midi/Track.html"
 * @see MidiMessage "https://docs.oracle.com/javase/jp/6/api/javax/sound/midi/MidiMessage.html"
 */

public class MidiAnalysis {
    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        String filePath = System.getenv("MIDI_FILE_PATH");
        Sequence sequence = MidiSystem.getSequence(new File(filePath));
        Track track = sequence.getTracks()[0];
        printMidiEvents(track);
    }

    private static void printMidiEvents(Track track) {
        Map<String, Object> midiMap = new LinkedHashMap<>();
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event      = track.get(i);
            long midiTime        = event.getTick();      // イベントのタイムスタンプ, MIDIティック単位
            MidiMessage message  = event.getMessage();
            int messageStatus    = message.getStatus();  // MIDIメッセージのステータスバイト
            long messageLength   = message.getLength();  // MIDIメッセージのデータ長
            byte[] messageData   = message.getMessage(); // MIDIメッセージデータ(ステータスバイト + データバイト)

            midiMap.put("TimeStamp", midiTime);
            midiMap.put("Status", messageStatus);
            midiMap.put("Length", messageLength);

            String messages = "";
            for (byte b : messageData) {
                // javaはbyteを符号付きで表現するので，符号無しint型に変換している
                messages += Byte.toUnsignedInt(b) + ",";
            }
            messages = messages.substring(0, messages.length() - 1);
            midiMap.put("messages", messages);

            System.out.println(midiMap);
            midiMap.clear();
        }
    }
}
