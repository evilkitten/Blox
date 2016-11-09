Include"Explosion.bb"

Const COLLISION_BAT=1
Const COLLISION_BALL=2
Const COLLISION_BRICK=4
Const COLLISION_BOUND=8
Const COLLISION_ALIEN=16

Function BatBoundary()
	;Because if the collisions are reset and ball collides with BAT AND BOUND and BAT colides with BOUND. Something's gonna give...
	
	Local X#=EntityX(BAT,True)
	
	If (X>=BOUNDARY_W)
		PositionEntity BAT, BOUNDARY_W-1,0,GENERIC_Z_OFFSET,True
	End If
	
	If (X=<(0-BOUNDARY_W))
		PositionEntity BAT, 0-(BOUNDARY_W-1),0,GENERIC_Z_OFFSET,True
	End If
	
End Function

Function BallBoundary(B.BALL)
	;Because if it's moving fast ernough and at the right angle the ball can escape boundary and will not trigger collision
	
	Local X#=EntityX(B\Entity,True)
	Local Y#=EntityY(B\Entity,True)
	
	If (X#>BOUNDARY_W)
		Bounce(B,BOUND_RIGHT,-1.0,0.0)
	End If
	If (X#<(0-BOUNDARY_W))
		Bounce(B,BOUND_LEFT,1,0.0)
	End If
	If ((Y#>BOUNDARY_H))
		Bounce(B,BOUND_UP,0.0,-1.0)
	End If
	
End Function

Function Bounce(B.BALL, Entity,nX#,nY#)	
	If (B<>Null)
		
		Local magX#=Float(Abs(B\VelX#))
		Local magY#=Float(Abs(B\VelY#))
		
		; Calculate Dot Product
		Local DotN# = (B\VelX#  * nX#) + (B\VelY# * nY# )
		
		; Calculate Normal force
		Local NFx# = -2.0 * nX# * DotN#
		Local NFy# = -2.0 * nY# * DotN#
		
		;Add Normal force
		B\VelX# = B\VelX# + NFx#
		B\VelY# = B\VelY# + NFy#
		
		Local SpeedModifier#
		
		
		If (Entity=BAT)Or(Entity=BATENDL)Or(Entity=BATENDR)Or(Entity=BATMID)
			SpeedModifier =	BAT_COLLISIONBALL_INFLUENCE*Sgn(BATSPEED)
		End If		
		
		B\VelX#=B\VelX# + (BAT_COLLISIONBALL_INFLUENCE*Sgn(B\VelX#))+SpeedModifier
		B\VelY#=B\VelY# + (BAT_COLLISIONBALL_INFLUENCE*Sgn(B\VelY#))
		
		;Prevent slowdown
		If Abs(B\VelX)<magX
			B\VelX#=Float(magX#*Sgn(B\VelX))
		End If
		
		If (Abs(B\VelY)<magY)
			B\VelY#=Float(magY#*Sgn(B\VelY))
		End If
		
		Local FlagForRageX=False
		Local FlagForRageY=False
		
		;limit ballspeed
		If (Abs(B\VelX#))>=BALL_MAX_SPEED
			B\VelX#=Sgn(B\VelX#)*BALL_MAX_SPEED 
			FlagForRageX=True
		End If
		
		If (Abs(B\VelY#))>=BALL_MAX_SPEED
			B\VelY#=Sgn(B\VelY#)*BALL_MAX_SPEED 
			FlagForRageY=True
		End If
		
		If (FlagForRageX) And (FlagForRageY)
			B\ShineColour=GetDarkColour(COL_SELECTION_RED)
			EntityColor B\Entity,BALL_RAGE_R,BALL_RAGE_G,BALL_RAGE_B
		Else
			B\ShineColour=B\OriginalShineColour
			EntityColor B\Entity,BALL_COLOR_R,BALL_COLOR_G,BALL_COLOR_B
		EndIf
		
		If (EntityCollided(B\Entity,COLLISION_BRICK)=Entity)
			
			;Hpoefully this should fix rare exception situations (typically multiballs both colliding with same brick)
			RemoveThisCollisionForAllObjects(Entity)
			;ResetEntity Entity
			;ResetEntity B\Entity
			
			Local BR.BRICK_DATA=Object.BRICK_DATA(Int(EntityName(Entity)))
			If (BR<>Null)
				AttemptToDestroyBrick(BR)
			End If
			
		Else
			If (EntityCollided(B\Entity,COLLISION_ALIEN)=Entity)
				KillAlien(GetAlienFromEntity(Entity))
			Else
				
			;Removed - There are serious issues with the Blitz3D collision system that make it impossible to have a CATCH special power up.
				
;				If (EntityCollided(B\Entity,COLLISION_BAT)=Entity)
;					If (Entity=BAT)
;						DebugLog("Bat and Ball collision")
;						ResetEntity B\Entity
;						ResetEntity Entity
;						CatchBall(B)
;					End If
;				End If
				
				ResetEntity B\Entity
				;ResetEntity Entity	
			End If
		End If
	End If
End Function

Function RemoveThisCollisionForAllObjects(Entity)
	Local Count=CountCollisions(Entity)
	Local Iter
	Local Coll
	For Iter=1 To Count
		Coll=CollisionEntity(Entity,Iter)
		ResetEntity Coll
	Next
	ResetEntity Entity
End Function

Function AlienBoundary(A.ALIEN)
	;Because everyone else has one
	
	Local X#=EntityX(A\Entity,True)
	Local Y#=EntityY(A\Entity,True)
	
	Local WLimit=Abs(BOUNDARY_W-ALIEN_BOUNDARY_INSET)
	Local HLimitTop=Abs(BOUNDARY_H-ALIEN_BOUNDARY_INSET)
	Local HLimitBottom=Abs(BOUND_DEATHZONE_Y*0.5)
	
	If (X>WLimit)
		X=(WLimit-1)
	End If
	
	If (X<(0-WLimit))
		X=0-(WLimit-1)
	End If
	
	If (Y>HLimitTop)
		Y=(HLimitTop-1)
	End If
	
	If (Y<HLimitBottom)
		Y=HLimitBottom+1
	End If
	
	PositionEntity A\Entity,X#,Y#,GENERIC_Z_OFFSET,True
	
	ResetEntity A\Entity
End Function

Function GetSpecialBatCollision(X#)
	Local L#=EntityX(BAT,True)-BAT_LENGTHSIZE
	Local R#=EntityX(BAT,True)+BAT_LENGTHSIZE
	
	;L=L-BAT_THICKSIZE
	;R=R+BAT_THICKSIZE
	
	Return ((X>L)  And (X<R))
End Function

Function RoundedTrig#(Result#)
	If (Abs(Result#)<0.1)
		Result#=Float(Int(Result#))
	End If
	Return Result#
End Function
;~IDEal Editor Parameters:
;~F#8#17#86#91#B0#BA
;~C#Blitz3D