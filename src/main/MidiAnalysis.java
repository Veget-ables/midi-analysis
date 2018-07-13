package main;

import javax.sound.midi.*;
import java.io.*;

/**
 * @see Sequence    "https://docs.oracle.com/javase/jp/6/api/javax/sound/midi/Sequence.html"
 * @see Track       "https://docs.oracle.com/javase/jp/6/api/javax/sound/midi/Track.html"
 * @see MidiMessage "https://docs.oracle.com/javase/jp/6/api/javax/sound/midi/MidiMessage.html"
 */

public class MidiAnalysis {
    private static final String MIDI_PATH = "c:\\Users\\Sample\\IdeaProjects\\MidConverter\\res\\";

    public static void main(String[] args) throws InvalidMidiDataException, IOException {
        Sequence sequence = MidiSystem.getSequence(new File(MIDI_PATH + "sample.MID"));

        // 1. 編集前のMIDIデータをtxtファイルに出力
        outputMidiSequence(sequence, MIDI_PATH + "sample_before.txt");

        // 2. 例: sequenceからstatusCode:144のeventを取り除く
        removeEvent(sequence, 144);

        // 3. 編集後のMIDIデータをtxtファイルに出力
        outputMidiSequence(sequence, MIDI_PATH + "sample_after.txt");

        // 4. 編集後のMIDIデータをMIDファイルに出力
        MidiSystem.write(sequence, 0, new File(MIDI_PATH + "sample_out.MID"));
    }

    /**
     * sequenceから対象のstatusCodeを持つeventを取り除く
     * @param sequence
     * @param statusCode 除外対象のstatusCode
     */
    private static void removeEvent(Sequence sequence, int statusCode){
        Track track = sequence.getTracks()[0];

        int index = 0;
        while (track.size() != index) {
            MidiEvent event = track.get(index);
            MidiMessage message = event.getMessage();
            int messageStatus = message.getStatus();
            if (messageStatus == statusCode) {
                track.remove(event);
            } else {
                index++;
            }
        }
    }

    /**
     * 現在のsequenceの状態を外部ファイルに書き出す
     * @param sequence
     * @param fileName
     * @throws IOException
     */
    private static void outputMidiSequence(Sequence sequence, String fileName) throws IOException {
        File file = new File(fileName);
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

        Track track = sequence.getTracks()[0];         // Sequenceから一つだけ存在するTrackを取得
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            long midiTime = event.getTick();           // イベントのタイムスタンプ, MIDIティック単位
            MidiMessage message = event.getMessage();
            int messageStatus = message.getStatus();   // MIDIメッセージのステータスバイト
            long messageLength = message.getLength();  // MIDIメッセージのデータ長
            byte[] messageData = message.getMessage(); // MIDIメッセージデータ(ステータスバイト + データバイト)

            String messages = "";
            for (byte b : messageData) {
                // javaはbyteを符号付きで表現するので，符号無しint型に変換している
                messages += Byte.toUnsignedInt(b) + ",";
            }
            messages = messages.substring(0, messages.length() - 1);

            String eventData = "TimeStamp:" + midiTime + "," +
                    "Status:" + messageStatus + "," +
                    "Length:" + messageLength + "," +
                    "messages:" + messages;

            pw.println(eventData);
        }
        pw.close();
    }
}
