ChangeDir "F:\bb\B3D\WIP\Blox\";Replace(systempropperty("appdir")+"\","\\","\")

Include"Data.bb"

Include"Include.bb"

Runtime

Global GAME_STATE_EXIT; Currently NO CONDITIONS EXIST to set this flag to true... preserved for future potential if necessary.

Function Runtime()
	Initialise
	Game
End Function

Function Game()
	StartGame
	GameLoop
	EndGame
End Function

Function GameLoop()
	While (Not(GAME_STATE_EXIT))
		
		;If KeyHit(1) Then GAME_STATE_EXIT=True
		
		FRAMETIMESTAMP=MilliSecs()
		
		ControlInput
		
		If ((MilliSecs()-LAUNCH_DELAY_TIMESTAMP)>LAUNCH_DELAY_DURATION_MAX)
			LaunchBall()
		End If
		
		Animation	
		
		CallRender
		
		AudioFeature
	Wend
	
	WriteLog("Escaped from Game Loop")
	
End Function

Function CheckDeath(B.BALL)
	If EntityY(B\Entity,True)<BOUND_DEATHZONE_Y
		RemoveBallFromPlay(B)
	End If
End Function

Function DoDeath()
	If (STATE=STATE_GAME)
		;There are rare occurrences where CPU is not able to cathch the ball.
		PlayPlayerSound(SND_PLAYER_LIFELOST)
		PLAYER_LIVES=PLAYER_LIVES-1
		WriteLog("Life Lost. Current lives now "+Str(PLAYER_LIVES))
		ShowRemainingLives
	End If
	
	If (PLAYER_LIVES)
		Reset
	Else
		;GAME_STATE_EXIT=True
		EndGame
	End If
	
End Function

Function AddScore(Points)
	If (STATE=STATE_GAME)
		If (Points)
			SCORE=SCORE+Points
			UpdateScoreBoard
			
			If (SCORE>SCORE_NEXT_NEWLIFE_TARGET)
				WriteLog("Score exceeds threshhold for extra life award")
				SCORE_NEXT_NEWLIFE_TARGET=SCORE_NEXT_NEWLIFE_TARGET+SCORE_NEWLIFE_THRESHOLD
				WriteLog("Next threshold established "+Str(SCORE_NEXT_NEWLIFE_TARGET))
				If (ExtraLifeAwardValid())	
					AwardExtraLife
				End If
			End If
		End If
	End If
End Function

Function AwardExtraLife()
	PlayPlayerSound(SND_PLAYER_LIFEGAINED)
	PLAYER_LIVES=PLAYER_LIVES+1
	WriteLog("Life awarded. Current lives now "+Str(PLAYER_LIVES))
	ShowRemainingLives
End Function

Function ExtraLifeAwardValid()
	If (PLAYER_LIVES<PLAYER_LIVESMAX)
		Return True
	Else
		WriteLog("player lives at maximum ("+Str(PLAYER_LIVESMAX)+")")
		Return False
	End If
End Function

Function SetScore(nScore=0)
	SCORE=nScore
	UpdateScoreBoard
End Function

Function NextRound()
	WriteLog("Next Round Called")
	
	If (STATE=STATE_ATTRACT)
		Local Previous=ROUND
		While (ROUND=Previous)
			ROUND=Rand(KNOWN_ROUNDS,KNOWN_ROUNDS+99)
		Wend
	Else
		ROUND=ROUND+1	
		
		If (ROUND>HIGHEST_STAGE_REACHED) 
			If (ROUND > 255)
				HIGHEST_STAGE_REACHED=255
			End If
			HIGHEST_STAGE_REACHED=ROUND
			WriteData
		End If
	End If
	
	ClearRound
	InitialiseRound
End Function

Function AddRoundFeatures()
	;DEPRECATED - (Silly)
;WriteLog("Adding Round Features")
	
	TurnEntity BACKDROP,0,0,Rand(0,3)*90.0,True
	
	If (ROUND > BACKDROP_TEXTURECOUNTFRAME)
;		Local FeatureLevel=(ROUND Mod 7)+1
;		If (FeatureLevel And 1)
;			DebugLog("Perspective")
;			OBJECT_SHADOWS_ON=False
;			RotateEntity BACKDROP,45,180,0,True
;		End If
		
;		If (FeatureLevel And 2)
;			ShowEntity MIRROR
;			EntityAlpha BACKDROP,MIRROR_REFRACTICVE_INDEX
;			RotateEntity MIRROR,45,180,0,True
;			DebugLog("Mirror")	
;		End If
		
;		If (FeatureLevel And 4)
;			;Inverted
;			DebugLog("Viewport")	
;			RotateEntity CAMERA,0,0,180,True
;		End If
		
		
		
		
	End If
	
End Function

Function Animation()
	BatMotion
	
	BallMotion
	
	UpdateBat
	UpdateAliens
	
	UpdateShadows
	UpdateShines
	UpdateBatInt
	UpdateExplosions
	UpdateSpecials
	UpdateBricks
End Function
;~IDEal Editor Parameters:
;~F#A#F#15#2D#33#45#57#5E#67#6C#84#A6
;~C#Blitz3D