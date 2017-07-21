Const SOUND_ATTRACTTHEME$="Attract.ogg"
Const SOUND_GAMEOVERTHEME$="Game Over.ogg"
Const SOUND_HISCORETHEME$="Hi Score.ogg"

Const AUDIO_DEFAULT_MUS_VOL#=0.85

Const THEMECOUNT=5

;Original Sample Frequency of 8000 Hz
Const AUDIO_RAW_FREQUENCY%=8000

Const AUDIO_PITCH_MODULATION_MAX#=1.0625
Const AUDIO_PITCH_MODULATION_MIN#=0.9375

Const AF_1=1
Const AF_2=2
Const AF_3=3
Const AF_4=4
Const AF_5=5

Global AUDIO_MUS_VOL#

Global CHN_THEME

Global CHN_SFX_MENU
Global CHN_SFX_BALL
Global CHN_SFX_BRICK
Global CHN_SFX_BAT
Global CHN_SFX_ALIEN
Global CHN_SFX_SPECIAL
Global CHN_SFX_PLAYER

Global SND_THEME

Global SND_MENU_CHANGE
Global SND_MENU_SELECT

Global SND_BALL_BOUNCE_BOUND
Global SND_BALL_BOUNCE_BAT

Global SND_BRICK_DESTROY
;Global SND_BRICK_DAMAGE
Global SND_BRICK_INVINCIBLE

Global SND_BRICK_IMPOSSIBLE_TIMEOUT

Global SND_BAT_BOUND

Global SND_ALIEN_SPAWN
Global SND_ALIEN_DESTROY

Global SND_PLAYER_LIFELOST
Global SND_PLAYER_LIFEGAINED

Global SND_SPECIAL_EXTEND
Global SND_SPECIAL_REDUCE
Global SND_SPECIAL_FADE
Global SND_SPECIAL_SLOW
Global SND_SPECIAL_MULTIBALL
Global SND_SPECIAL_SCORE

Global AUDIO_TIMESTAMP

Global AUDIO_MATCH_FIRST
Global AUDIO_MATCH_SECOND
Global AUDIO_MATCH_THIRD
Global AUDIO_MATCH_FOURTH
Global AUDIO_MATCH_FIFTH
Global AUDIO_MATCH_END

Global CURRENT_AUDIO_FEATURE
Global AF_SCROLL_U#
Global AF_SCROLL_V#

Function PlayBallSound(BallSound,Modulate=False)
	If (CHN_SFX_BALL)
		If ChannelPlaying(CHN_SFX_BALL)
			StopChannel CHN_SFX_BALL
		End If
	End If	
	
	If 	(STATE<>STATE_ATTRACT)
		;Modulation
		If (Modulate)
			Local Modulation#=Rnd#(AUDIO_PITCH_MODULATION_MIN#,AUDIO_PITCH_MODULATION_MAX#)
			SoundPitch BallSound,(AUDIO_RAW_FREQUENCY*Modulation)
		Else
			SoundPitch BallSound,AUDIO_RAW_FREQUENCY
		End If
		
		CHN_SFX_BALL=PlaySound(BallSound)
	End If
End Function		

Function PlayBrickSound(BrickSound)
	If (CHN_SFX_BRICK)
			If ChannelPlaying(CHN_SFX_BRICK)
				StopChannel CHN_SFX_BRICK
			End If
		End If	
		
		If 	(STATE<>STATE_ATTRACT)
			CHN_SFX_BRICK=PlaySound(BrickSound)	
	End If
End Function		

Function PlayBatSound(BatSound)
	If (CHN_SFX_BAT)
			If ChannelPlaying(CHN_SFX_BAT)
				StopChannel CHN_SFX_BAT
			End If
		End If	
		
		If 	(STATE<>STATE_ATTRACT)
		CHN_SFX_BAT=PlaySound(BatSound)	
	End If
End Function		

Function PlayAlienSound(AlienSound)
		If (CHN_SFX_ALIEN)
			If ChannelPlaying(CHN_SFX_ALIEN)
				StopChannel CHN_SFX_ALIEN
			End If
		End If	
		
		If 	(STATE<>STATE_ATTRACT)
		CHN_SFX_ALIEN=PlaySound(AlienSound)	
	End If
End Function		

Function PlaySpecialSound(SpecialSound)
	If (CHN_SFX_SPECIAL)
		If ChannelPlaying(CHN_SFX_SPECIAL)
			StopChannel CHN_SFX_SPECIAL
		End If
	End If	
	
	If 	(STATE<>STATE_ATTRACT)
		CHN_SFX_SPECIAL=PlaySound(SpecialSound)	
	End If
End Function

Function PlayPlayerSound(PlayerSound)
	If (CHN_SFX_PLAYER)
		If ChannelPlaying(CHN_SFX_PLAYER)
			StopChannel CHN_SFX_PLAYER
		End If
	End If	
	
	If 	(STATE<>STATE_ATTRACT)
		CHN_SFX_PLAYER=PlaySound(PlayerSound)
	End If
End Function

Function AudioFeature()
	If (STATE=STATE_ATTRACT)
		
		If ((MilliSecs()-ATTRACT_DEMO_TIMESTAMP)>ATTRACT_DEMO_DURATION)
			WriteLog("Attract Mode round demonstration time ("+Str(ATTRACT_DEMO_DURATION*0.001)+") exceeded for this round.")
			NextRound
		End If
	Else
		If (STATE=STATE_GAME)
		
			Local Stage= (ROUND Mod THEMECOUNT)
			
			Select CURRENT_AUDIO_FEATURE
				Case AF_1:
					If ((FRAMETIMESTAMP-AUDIO_TIMESTAMP)>AUDIO_MATCH_SECOND)
						CURRENT_AUDIO_FEATURE=AF_2
						ApplyAudioFeature(Stage)
					End If
				Case AF_2:
					If ((FRAMETIMESTAMP-AUDIO_TIMESTAMP)>AUDIO_MATCH_THIRD)
						CURRENT_AUDIO_FEATURE=AF_3
						ApplyAudioFeature(Stage)
					End If
				Case AF_3:
					If ((FRAMETIMESTAMP-AUDIO_TIMESTAMP)>AUDIO_MATCH_FOURTH)
						CURRENT_AUDIO_FEATURE=AF_4
						ApplyAudioFeature(Stage)
					End If
				Case AF_4:
					If ((FRAMETIMESTAMP-AUDIO_TIMESTAMP)>AUDIO_MATCH_FIFTH)
						CURRENT_AUDIO_FEATURE=AF_5
						ApplyAudioFeature(Stage)
					End If	
				Case AF_5:
					If ((FRAMETIMESTAMP-AUDIO_TIMESTAMP)>AUDIO_MATCH_END)
						Local Offset=(MilliSecs()-AUDIO_TIMESTAMP) - AUDIO_MATCH_END
						CURRENT_AUDIO_FEATURE=0
						AUDIO_TIMESTAMP=MilliSecs()-Offset
						ApplyAudioFeature(Stage)
					End If
					
				Default:
					If ((MilliSecs()-AUDIO_TIMESTAMP)>AUDIO_MATCH_FIRST)
						CURRENT_AUDIO_FEATURE=AF_1
						ApplyAudioFeature(Stage)
					End If
			End Select
		
			MoveEntity BACKDROP,AF_SCROLL_U,0,AF_SCROLL_V;,True
		End If
	End If
End Function

Function ApplyAudioFeature(n)
	
	Select (n)
			
		Case 0:
			
			Select (CURRENT_AUDIO_FEATURE)
				Case AF_1:
					AF_SCROLL_U=-0.01
					AF_SCROLL_V=0.00
					
				Case AF_2:
					AF_SCROLL_U=0.0
					AF_SCROLL_V=-0.01
					
				Case AF_3:
					AF_SCROLL_U=-0.2
					AF_SCROLL_V=0.0
					
				Case AF_4:
					AF_SCROLL_U=0.0
					AF_SCROLL_V=-0.2
					
				Case AF_5:
					AF_SCROLL_U=0.0
					AF_SCROLL_V=0.2
					
				Default:
					AF_SCROLL_U#=0.0
					AF_SCROLL_V#=0.0
			End Select
			
		Case 1:
			
			Select (CURRENT_AUDIO_FEATURE)
				Case AF_1:
					AF_SCROLL_U=-0.01
					AF_SCROLL_V=0.01
					
				Case AF_2:
					AF_SCROLL_U=0.01
					AF_SCROLL_V=0.01
				Case AF_3:
					AF_SCROLL_U=-0.01
					AF_SCROLL_V=0.01
					
				Case AF_4:
					AF_SCROLL_U=0.01
					AF_SCROLL_V=0.01
					
				Case AF_5:
					AF_SCROLL_U=-0.01
					AF_SCROLL_V=0.01
					
				Default:
					AF_SCROLL_U=0.01
					AF_SCROLL_V=0.01
			End Select
			
			
		Case 2:
			
			Select (CURRENT_AUDIO_FEATURE)
				Case AF_1:
					AF_SCROLL_U=-0.1
					AF_SCROLL_V=0.1
					
				Case AF_2:
					AF_SCROLL_U=0.1
					AF_SCROLL_V=0.1
				Case AF_3:
					AF_SCROLL_U=-0.1
					AF_SCROLL_V=0.1
					
				Case AF_4:
					AF_SCROLL_U=0.1
					AF_SCROLL_V=-0.1
					
				Case AF_5:
					AF_SCROLL_U=0.0
					AF_SCROLL_V=0.2
					
				Default:
					AF_SCROLL_U=0.00
					AF_SCROLL_V=-0.01
			End Select
			
		Case 3:
			
			Select (CURRENT_AUDIO_FEATURE)
				Case AF_1:
					AF_SCROLL_U=0.0
					AF_SCROLL_V=0.1
					
				Case AF_2:
					AF_SCROLL_U=0.0
					AF_SCROLL_V=0.15
					
				Case AF_3:
					AF_SCROLL_U=0
					AF_SCROLL_V=0.2
					
				Case AF_4:
					AF_SCROLL_U=0.0
					AF_SCROLL_V=-0.1
					
				Case AF_5:
					AF_SCROLL_U=0.0
					AF_SCROLL_V=0.15
					
				Default:
					AF_SCROLL_U=0.00
					AF_SCROLL_V=-01
			End Select	
			
		Case 4:
			
			Select (CURRENT_AUDIO_FEATURE)
				Case AF_1:
					AF_SCROLL_U=0.05
					AF_SCROLL_V=0.0
					
				Case AF_2:
					AF_SCROLL_U=-0.05
					AF_SCROLL_V=0.0
					
				Case AF_3:
					AF_SCROLL_U=0.05
					AF_SCROLL_V=0.0
					
				Case AF_4:
					AF_SCROLL_U=-0.05
					AF_SCROLL_V=0.0
					
				Case AF_5:
					AF_SCROLL_U=-0.05
					AF_SCROLL_V=0.0
					
				Default:
					AF_SCROLL_U=0.00
					AF_SCROLL_V=0
			End Select	
			
			
			
	End Select
End Function

Function AudioPoints()
	Select (ROUND Mod THEMECOUNT)
		Case 0:
			AUDIO_MATCH_FIRST=19600
			AUDIO_MATCH_SECOND=35500
			AUDIO_MATCH_THIRD=67000
			AUDIO_MATCH_FOURTH=114500
			AUDIO_MATCH_FIFTH=193000
			AUDIO_MATCH_END=256000
			
		Case 1:
			AUDIO_MATCH_FIRST=00000
			AUDIO_MATCH_SECOND=4000
			AUDIO_MATCH_THIRD=8000
			AUDIO_MATCH_FOURTH=12000
			AUDIO_MATCH_FIFTH=16000
			AUDIO_MATCH_END=24000
			
		Case 2:
			AUDIO_MATCH_FIRST=11300
			AUDIO_MATCH_SECOND=53650
			AUDIO_MATCH_THIRD=76250
			AUDIO_MATCH_FOURTH=120000
			AUDIO_MATCH_FIFTH=152500
			AUDIO_MATCH_END=284000
			
		Case 3:
			AUDIO_MATCH_FIRST=20050
			AUDIO_MATCH_SECOND=60000
			AUDIO_MATCH_THIRD=100110
			AUDIO_MATCH_FOURTH=160800
			AUDIO_MATCH_FIFTH=180200
			AUDIO_MATCH_END=200200
			
		Case 4:
			AUDIO_MATCH_FIRST=27500
			AUDIO_MATCH_SECOND=63500
			AUDIO_MATCH_THIRD=135700
			AUDIO_MATCH_FOURTH=171400
			AUDIO_MATCH_FIFTH=226200
			AUDIO_MATCH_END=252200		
			
	End Select
End Function

Function ChangeMusic(ThemePath$,NotifyThemeChange=True)
	If (CHN_THEME)
		If (ChannelPlaying(CHN_THEME))
			WriteLog("Stopping existing current theme music")
			
			StopChannel CHN_THEME
			FreeSound SND_THEME
		End If
	End If
	
	SND_THEME =LoadSound(SOUNDPATH+ThemePath)
	LoopSound SND_THEME
	
	CHN_THEME=PlaySound(SND_THEME)
	ChannelVolume CHN_THEME,AUDIO_MUS_VOL#
	
	If (NotifyThemeChange) Then WriteLog("Theme music set to "+ThemePath)
	CURRENT_AUDIO_FEATURE=0
	AUDIO_TIMESTAMP=MilliSecs()
End Function
;~IDEal Editor Parameters:
;~F#4A#5E#6A#76#82#8E#9A#CF#163#190
;~C#Blitz3D