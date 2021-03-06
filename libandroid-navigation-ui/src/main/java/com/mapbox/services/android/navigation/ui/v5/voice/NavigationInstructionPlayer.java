package com.mapbox.services.android.navigation.ui.v5.voice;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.mapbox.services.android.navigation.v5.milestone.VoiceInstructionMilestone;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NavigationInstructionPlayer implements InstructionListener {

  private AudioManager instructionAudioManager;
  private AudioFocusRequest instructionFocusRequest;
  private MapboxSpeechPlayer mapboxSpeechPlayer;
  private AndroidSpeechPlayer androidSpeechPlayer;
  private Queue<VoiceInstructionMilestone> instructionQueue;
  private boolean isMuted;

  public NavigationInstructionPlayer(@NonNull Context context, String language, String accessToken) {
    initAudioManager(context);
    initAudioFocusRequest();
    initInstructionPlayers(context, language, accessToken);
    instructionQueue = new ConcurrentLinkedQueue<>();
  }

  public void play(VoiceInstructionMilestone voiceInstructionMilestone) {
    instructionQueue.add(voiceInstructionMilestone);
    mapboxSpeechPlayer.play(voiceInstructionMilestone.getSsmlAnnouncement());
  }

  public boolean isMuted() {
    return isMuted;
  }

  public void setMuted(boolean isMuted) {
    this.isMuted = isMuted;
    mapboxSpeechPlayer.setMuted(isMuted);
    androidSpeechPlayer.setMuted(isMuted);
  }

  public void onOffRoute() {
    instructionQueue.clear();
    mapboxSpeechPlayer.onOffRoute();
    androidSpeechPlayer.onOffRoute();
  }

  public void onDestroy() {
    mapboxSpeechPlayer.onDestroy();
    androidSpeechPlayer.onDestroy();
  }

  @Override
  public void onStart() {
    requestAudioFocus();
    instructionQueue.poll();
  }

  @Override
  public void onDone() {
    abandonAudioFocus();
  }

  @Override
  public void onError(boolean isMapboxPlayer) {
    if (isMapboxPlayer) {
      androidSpeechPlayer.play(instructionQueue.peek().getAnnouncement());
    } else {
      instructionQueue.poll();
    }
  }

  private void initAudioFocusRequest() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      instructionFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK).build();
    }
  }

  private void initInstructionPlayers(Context context, String language, String accessToken) {
    mapboxSpeechPlayer = new MapboxSpeechPlayer(context, language, accessToken);
    mapboxSpeechPlayer.setInstructionListener(this);
    androidSpeechPlayer = new AndroidSpeechPlayer(context, language);
    androidSpeechPlayer.setInstructionListener(this);
  }

  private void initAudioManager(Context context) {
    instructionAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
  }

  private void requestAudioFocus() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      instructionAudioManager.requestAudioFocus(instructionFocusRequest);
    } else {
      instructionAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC,
        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
    }
  }

  private void abandonAudioFocus() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      instructionAudioManager.abandonAudioFocusRequest(instructionFocusRequest);
    } else {
      instructionAudioManager.abandonAudioFocus(null);
    }
  }
}
