Const SOUND_ATTRACTTHEME$="Attract.ogg"
Const SOUND_GAMEOVERTHEME$="Game Over.ogg"
Const SOUND_HISCORETHEME$="Hi Score.ogg"

Const AUDIO_DEFAULT_MUS_VOL#=0.85

Const THEMECOUNT=5

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

Global SND_THEME

Global SND_BRICK_DESTROY
;Global SND_BRICK_DAMAGE
Global SND_BRICK_INVINCIBLE

Global SND_ALIEN_SPAWN
Global SND_ALIEN_DESTROY

Global SND_BALL_BOUNCE_BOUND
Global SND_BALL_BOUNCE_BAT

Global SND_BAT_BOUND

Global SND_SPECIAL_EXTEND
Global SND_SPECIAL_REDUCE
Global SND_SPECIAL_MULTIBALL
Global SND_SPECIAL_SCORE

Global SND_BRICK_IMPOSSIBLE_TIMEOUT

Global SND_PLAYER_LIFELOST
Global SND_PLAYER_LIFEGAINED

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
;~F#3E#73#107
;~C#Blitz3D