Const EXP_SHARDS=24
Const EXP_SHARD_FX=48
Const EXP_SHARD_SPEED#=0.005
Const EXP_LIFETIME=6000

Type EXPLOSION
	Field X#
	Field Y#
	Field Shine.Shine
	Field Shards[EXP_SHARDS-1]
	Field Timestamp
End Type	

Type EXPLOSIONSHARDS
	Field Parent.Explosion
	Field vX#
	Field vY#
	Field Entity
End Type

Function CreateExplosion(X#,Y#,R,G,B,Transp=True)
	Local E.Explosion=New Explosion
	Local S
	
	E\Shine.Shine=CreateShine(X,Y,BRICKW Shl True,BRICKH Shl True)
	
	For S=0 To EXP_SHARDS-1
		E\Shards[S]=CreateShard(E,X#,Y#,R,G,B,Transp)
	Next
	
	E\Timestamp=MilliSecs()
End Function

Function CreateShard(Parent.EXPLOSION,X#,Y#,R,G,B,Transp=True)
	Local S.EXPLOSIONSHARDS=New EXPLOSIONSHARDS
	S\Parent=Parent
	
	S\Entity=CreateMesh()
	Local Surf=CreateSurface(S\Entity)
	Local v0=AddVertex(Surf,0,0,0,0,1)
	Local v1=AddVertex(Surf,0,1,0,0,0)
	Local v2=AddVertex(Surf,1,1,0,1,0)
	
	AddTriangle(Surf,v0,v1,v2)
	
;	VertexColor(Surf,v0,R,G,B,Transp*BRICK_TRANSPARENCY)
;	VertexColor(Surf,v1,R,G,B,Transp*BRICK_TRANSPARENCY)
;	VertexColor(Surf,v2,R,G,B,Transp*BRICK_TRANSPARENCY)
	
	EntityColor S\Entity,R,G,B
	EntityAlpha S\Entity,BRICK_TRANSPARENCY
	EntityFX S\Entity,EXP_SHARD_FX
	EntityShininess S\Entity,BRICK_SPECULAR
	
	PositionEntity S\Entity,X,Y,GENERIC_Z_OFFSET
	RotateEntity S\Entity,Rand(0,360),Rand(0,360),Rand(0,360),True
	S\vX=Rnd(0-1,1)
	S\vY=Rnd(0-1,1)
	
	Return Handle(S)
End Function

Function UpdateExplosions()
	Local E.EXPLOSION
	For E=Each EXPLOSION
		If ((FRAMETIMESTAMP-E\Timestamp)>EXP_LIFETIME)
			DestroyExplosion(E)
		Else
			Local S
			Local ES.EXPLOSIONSHARDS
			For S=0 To EXP_SHARDS-1
				ES=Object.EXPLOSIONSHARDS(E\Shards[S])
				If (ES<>Null)
					UpdateExplosionShard(ES)
				End If
			Next
		End If
	Next
End Function

Function DestroyExplosion(E.EXPLOSION)
	Local ES.EXPLOSIONSHARDS
	Local S
	
	For S=0 To EXP_SHARDS-1
		ES=Object.EXPLOSIONSHARDS(E\Shards[S])
		If (ES<>Null)
			FreeEntity ES\Entity
			Delete ES
		End If
	Next
	
	If (E\Shine<>Null)
		FreeEntity E\Shine\Entity
		Delete E\Shine
	End If
	
	Delete E
End Function

Function UpdateExplosionShard(ES.EXPLOSIONSHARDS)
	Local DampX#=EXP_SHARD_SPEED*(0.0-(Sgn(ES\vX)))
	Local DampY#=0-(EXP_SHARD_SPEED)
	
	ES\vX=ES\vX+DampX
	ES\vY=ES\vY+DampY
	
	TurnEntity ES\Entity,Sgn((ES\Parent\X-EntityX(ES\Entity,True))),Sgn((ES\Parent\Y-EntityY(ES\Entity,True))),True
	TranslateEntity ES\Entity,ES\vX,ES\vY,0,True
	
	If (EntityY(ES\Entity,True)<BOUND_DEATHZONE_Y)
		FreeEntity ES\Entity
		Delete ES
	End If
End Function
		
;~IDEal Editor Parameters:
;~F#5#D#14#21#3E#50#64
;~C#Blitz3D