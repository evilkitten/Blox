Const PLAYER_LIVESMAX=3

Const DEF_HISCORE=10000
Const DEF_INITS$="EKD"
Const MAX_INITS=3

Const LAUNCH_DELAY_DURATION_MAX=5000

Const STATE_ATTRACT=0
Const STATE_GAME=1
Const STATE_HISCORE_ENTRY=2

Const ATTRACT_DEMO_DURATION=40000
Const GAMEOVER_DURATION=155000
Const SCORE_NEWLIFE_THRESHOLD=10000

Const IMPOSSIBLE_BRICK_ROUND_TIMEOUT=600000;10 minutes

Const DEF_HIGHEST_STAGE=0
Global HIGHEST_STAGE_REACHED;(Capped at 255)

Global STATE

Global PLAYER_LIVES

Global LAUNCH_DELAY_TIMESTAMP
Global ATTRACT_DEMO_TIMESTAMP

Global ROUND

Global LAUNCH

Global SCORE
Global HISCORE
Global HISCOREINITS$

Global SCORE_NEXT_NEWLIFE_TARGET

Function EndGame()
	WriteLog("End Game")
	ClearRound
	
	If (SCORE>=HISCORE)
		NewHiScore
	End If
	
	GameOver
	ScrollCredits
	GAME_STATE_EXIT=False
	
	Game
End Function

Function NewHiScore()
	ChangeToHiScoreEntryMode
	HiScoreEntry
	
	WriteLog("HiScore has changed. Attempting to write changes to disk")
	WriteData
	
	FlushKeys
	FlushJoy
	
	UpdateHiScoreBoard
End Function

Function GameOver()
	WriteLog("Game Over called")
	ChangeMusic(SOUND_GAMEOVERTHEME)
	
	FlushKeys
	FlushJoy
	
	Local GameOverImage=UI_DisplayTextImage("GAMEOVER")
	
	MidHandle GameOverImage
	Local Exitstate
	
	Local GameOverDurationTimeStamp=MilliSecs()
	
	While (Not(Exitstate))
		RenderWorld
			
		DrawImage GameOverImage,GWIDTH*0.5,GHEIGHT*0.5
			
		Flip True
		
		If ((KeyHit(CTRL_KEY_LAUNCH)) Or (JoyHit(CTRL_JOY_LAUNCH,JOY_PORT)))
			Exitstate=True
		End If
		
		If ((MilliSecs()-GameOverDurationTimeStamp)>GAMEOVER_DURATION)
			Exitstate=True
		End If
	Wend
	
	FreeImage GameOverImage
End Function

Function ClearRound()
	WriteLog("Commencing cleanup of round data")
	RemoveAllBricks
	RemoveAllBalls
	RemoveAllSpecials
	DestroyAllAliens
	ClearCollisions
	InitialisePhysics
End Function

Function ChangeToAttractMode(NotifyThemeChange=True)
	If (NotifyThemeChange) Then WriteLog("State is now Attract Mode")
	STATE=STATE_ATTRACT
	CameraProject CAMERA,0,0,0
	HUD_SCOREBOARDX=GWIDTH-ImageWidth(HUD_HISCOREBOARD)
	HUD_SCOREBOARDY=0+(HUD_SCORE_CHARACTERHEIGHT Shr True);
	
	ATTRACT_DEMO_TIMESTAMP=MilliSecs()
	ChangeMusic(SOUND_ATTRACTTHEME,NotifyThemeChange)
End Function

Function ChangeToHiScoreEntryMode()
	WriteLog("State is now HiScoreEntry Mode")
	STATE=STATE_HISCORE_ENTRY
	CameraProject CAMERA,0,0,0
	HUD_SCOREBOARDX=GWIDTH-ImageWidth(HUD_SCOREBOARD)
	HUD_SCOREBOARDY=0+(HUD_SCORE_CHARACTERHEIGHT Shr True);
	
	ATTRACT_DEMO_TIMESTAMP=MilliSecs()
	ChangeMusic(SOUND_HISCORETHEME)
End Function
	
Function ChangeToGameMode()
	WriteLog("State is now Game Mode")
	CleanUI
	STATE=STATE_GAME
	CameraProject CAMERA,0,0,0
	HUD_SCOREBOARDX=HUD_SCORE_CHARACTERWIDTH;GWIDTH-ImageWidth(HUD_SCOREBOARD)
	HUD_SCOREBOARDY=ProjectedY();0+(HUD_SCORE_CHARACTERHEIGHT Shr True);
End Function

Function StartGame()
	WriteLog("Preparing to commence Game Loop")
	
	ResetLivesAndScore
	
	If (STATE=STATE_ATTRACT)
		ROUND=Rand(KNOWN_ROUNDS+1,99)
		WriteLog("Starting Attract Mode Game")
		HideRemainingLives
	Else
		Cls
		ShowRemainingLives()
		RenderWorld
		Flip True
		FlushKeys
		FlushJoy
		ROUND=0
		WriteLog("Starting Game")
	End If
	
	InitialiseRound
	
End Function

Function CheckForImpossibleBrickTimeout()
	;To prevent expåloit, an incrementing variable is used (which only increases with frames) rather than millisecond timer. 
	If (IMPOSSIBLE_BRICKS_REMOVED=False)
		If (INCREASING_FRAME_TIMER>(IMPOSSIBLE_BRICK_ROUND_TIMEOUT/MAX_FRAME_INTERVAL))
			RemoveImpossibleBricks
			IMPOSSIBLE_BRICKS_REMOVED=True
		End If
	End If
End Function

Function ResetLivesAndScore()
	PLAYER_LIVES=PLAYER_LIVESMAX
	SetScore
	SCORE_NEXT_NEWLIFE_TARGET=SCORE_NEWLIFE_THRESHOLD
End Function

Function Reset()
	WriteLog("Resetting Bat and Ball")
	
	FadeOff
	ResetBatSize
	
	PositionEntity BAT,0,0,GENERIC_Z_OFFSET,True
	
	CatchBall(SpawnBall())
	
	Local ShineColour=(COLOURS_SELECTION-BACKDROP_COL)+1
	
	CAUGHT\OriginalShineColour=GetLightColour(ShineColour)
	
	PositionEntity CAUGHT\Entity,0,BAT_THICKSIZE+(MeshHeight(CAUGHT\Entity)*0.5)+0.1,GENERIC_Z_OFFSET,True
	EntityParent CAUGHT\Entity,BAT,True
	
	ResetEntity CAUGHT\Entity
	ResetEntity BAT
	
End Function
;~IDEal Editor Parameters:
;~F#26#35#42#63#6D#78#83#8C#A4#AE#B4
;~C#Blitz3D