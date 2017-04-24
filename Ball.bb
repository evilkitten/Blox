Type BALL
	Field Entity
	Field VelX#
	Field VelY#
	Field Shadow.SHADOW
	Field ShineColour
	Field OriginalShineColour
	Field Angle ;Only used determination of catching and launching
End Type

Const BALL_SIZE#=1.0
Const BALL_COLOR_R=212
Const BALL_COLOR_G=212
Const BALL_COLOR_B=240

Const BALL_RAGE_R=212
Const BALL_RAGE_G=128
Const BALL_RAGE_B=96

Const BALL_SPECULAR#=0.999

Const BALLINITIALANGLE=315
Const BALL_INITIALSPEED#=0.3
Const BALL_MAX_SPEED#=1.0

Const BALL_SHINE_SIZE#=3.4
Const BALL_SHINE_TRANSPARENCY#=0.667

Const MAX_BALLS_IN_PLAY=5

Global BALLSINPLAY
Global CAUGHT.BALL

Function SlowBall()
	Local B.BALL
	For B=Each BALL
		B\VelX=BALL_INITIALSPEED*Sgn(B\VelX)*0.3
		B\VelY=BALL_INITIALSPEED*Sgn(B\VelY)*0.3
	Next
End Function

Function SpawnBall.BALL(Angle=BALLINITIALANGLE,Speed#=BALL_INITIALSPEED)
	;DONT FORGET TO ADD SHINE COLOUR
	BALLSINPLAY=BALLSINPLAY+1
	
	Local B.BALL=New BALL
	
	B\Angle=Angle
	
	B\Entity=CreateSphere(POLYCOUNT_DETAIL)
	ScaleMesh B\Entity,BALL_SIZE,BALL_SIZE,BALL_SIZE
	EntityColor B\Entity,BALL_COLOR_R,BALL_COLOR_G,BALL_COLOR_B
	EntityShininess B\Entity,BALL_SPECULAR
	
	PositionEntity B\Entity,0,BAT_THICKSIZE+(MeshHeight(B\Entity)*0.5),GENERIC_Z_OFFSET,True
	
	EntityRadius B\Entity,BALL_SIZE,BALL_SIZE
	;EntityBox B\Entity,0,0,0,BALL_SIZE,BALL_SIZE,BALL_SIZE
	
	EntityType B\Entity,COLLISION_BALL
	
	B\VelX=Sin(Angle)*Speed
	B\VelY=Cos(Angle)*Speed
	
	BallShadow(B)
	
;	WriteLog("Ball spawned "+Str[B.BALL])
	
	Return B
End Function

Function CatchBall(B.BALL)
	If (CAUGHT<>Null)
		LaunchBall
	End If
	CAUGHT=B
	LAUNCH=False
	EntityParent CAUGHT\Entity,BAT,True
	
	ResetEntity BAT
	ResetEntity CAUGHT\Entity
	
	If (STATE=STATE_ATTRACT)
		LAUNCH_DELAY_TIMESTAMP=MilliSecs()-LAUNCH_DELAY_DURATION_MAX
	Else
		LAUNCH_DELAY_TIMESTAMP=MilliSecs()
	End If 
	
	WriteLog("Ball caught: "+Str(CAUGHT))
End Function

Function RemoveBallFromPlay(B.BALL)
	ResetEntity B\Entity
	
	FreeShadow(B\Shadow)
	
	FreeEntity B\Entity
	Delete B
	BALLSINPLAY=BALLSINPLAY-1
	
	If (BALLSINPLAY<1)
		DoDeath
	End If
End Function

Function BallShine(B.BALL)
	CreateShine(EntityX(B\Entity,True),EntityY(B\Entity,True),BALL_SHINE_SIZE,BALL_SHINE_SIZE,B\ShineColour,BALL_SHINE_TRANSPARENCY)
End Function

Function RemoveAllBalls()
	Local B.BALL
	
	;Bit of a kludge to Prevent death
	BALLSINPLAY=99999
	
	For B=Each BALL
		RemoveBallFromPlay(B)
	Next
	
	; Restore kludged value
	BALLSINPLAY=0
End Function

Function BallMotion()
	Local B.BALL
	For B=Each BALL
		If (B<>CAUGHT)
			TranslateEntity B\Entity,B\VelX,B\VelY,0,True
			If CountCollisions(B\Entity)
				Local Collide=CollisionEntity(B\Entity,1)
				If (Collide)
					Local X#=CollisionNX(B\Entity,1)
					Local Y#=CollisionNY(B\Entity,1)
					Bounce(B,Collide,X,Y)
				End If
			End If
			
			If (B<>Null)
				;Clause required in case ball destroyed in collision- i.e. end of round
				
				PositionEntity B\Entity,EntityX(B\Entity,True),EntityY(B\Entity,True),GENERIC_Z_OFFSET,True
				
				BallShine(B)
				
				BallBoundary(B)
				CheckDeath(B)
				
			End If
			
		End If
	Next
End Function

Function AdditionalBallShineColour()
	Local Random=Rand(COL_SELECTION_GREEN,COLOURS_SELECTION)
	If (Random=BACKDROP_COL)
		Random=Random+1
	End If
	
	Local C=GetLightColour(Random)
	Return C
End Function

Function LaunchBall()
	LAUNCH=True
	
	If (CAUGHT<>Null)
		;WriteLog("Ball Launched "+Str[CAUGHT])
		EntityParent CAUGHT\Entity,0,True
		
		CAUGHT\VelX=Sin(CAUGHT\Angle)*BALL_INITIALSPEED
		CAUGHT\VelY=Cos(CAUGHT\Angle)*BALL_INITIALSPEED
		
		ResetEntity CAUGHT\Entity
		ResetEntity BAT
		
		If (CAUGHT\ShineColour=0)
			CAUGHT\ShineColour=CAUGHT\OriginalShineColour
		End If
		
		CAUGHT=Null
	End If
End Function
;~IDEal Editor Parameters:
;~F#0#21#29#47#5B#69#6D#7B#99#A3
;~C#Blitz3D