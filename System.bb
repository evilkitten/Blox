Const GRAPHICSPATH$="Graphics\"
Const SOUNDPATH$="Sound\"
Const DATAFILE$="lib.dat"
Const CONFIGFILE$="config.ini"
Const LOGFILE$="debug.log"

Global LOGPATH$
Global CONFIGPATH$
Global DATAPATH$

Global DEBUG=True

Const CONF_CONTROL_METHOD$="Control Method"
Const CONF_KEY_LEFT$="Keybind Left"
Const CONF_KEY_RIGHT$="Keybind Right"
Const CONF_KEY_LAUNCH$="Keybind Launch"
Const CONF_JOY_LAUNCH$="Control Button Launch"
Const CONF_JOY_DEADZONE$="Controller Deadzone"
Const CONF_JOY_PORT$="Controller Reference"
Const CONF_VOL_MUS$="Music Volume"


Function ReadData()
	Local File=ReadFile(DATAPATH)
	
	If (Not(File))
;		RuntimeError("Cannot access file: "+DATAPATH)
		InitialiseDefaultData
		Else	
		
		HISCOREINITS=Chr(ReadByte(File))+Chr(ReadByte(File))+Chr(ReadByte(File))
		HISCORE=ReadInt(File)
		HIGHEST_STAGE_REACHED=ReadByte(File)
		CloseFile File
	End If
	
End Function

Function ReadConfig()
	Local File=ReadFile(CONFIGPATH)
	If (File)
	ReadConfigLines(File)
	CloseFile File
End If

End Function

Function WriteData()
	Local File=WriteFile(DATAPATH)
	If (Not(File))
		Return
	End If
	
	WriteByte File,Asc(Mid(HISCOREINITS,1,1))
	WriteByte File,Asc(Mid(HISCOREINITS,2,1))
	WriteByte File,Asc(Mid(HISCOREINITS,3,1))
	WriteInt File,HISCORE
	WriteByte File,HIGHEST_STAGE_REACHED And 255
	CloseFile File
End Function

Function WriteConfig()
	Local File=WriteFile(CONFIGPATH)
	If (Not(File))
		Return
	End If
	
	If (CURRENT_CONTROL_METHOD<>CONTROL_DEFAULT_METHOD) Then WriteLine File,CONF_CONTROL_METHOD+"="+Str((CURRENT_CONTROL_METHOD And 1))
	
	If (CTRL_KEY_LEFT<>KEY_LEFT) Then WriteLine File,CONF_KEY_LEFT$+"="+Str(CTRL_KEY_LEFT)
	If (CTRL_KEY_RIGHT<>KEY_RIGHT) Then WriteLine File,CONF_KEY_RIGHT$+"="+Str(CTRL_KEY_RIGHT)
	If (CTRL_KEY_LAUNCH<>KEY_SPACE) Then WriteLine File,CONF_KEY_LAUNCH$+"="+Str(CTRL_KEY_LAUNCH)
	
	If (CTRL_JOY_LAUNCH<>JOY_DEFAULT_LAUNCH) Then WriteLine File,CONF_JOY_LAUNCH$+"="+Str(CTRL_JOY_LAUNCH)
	
	If (JOY_DEADZONE<>JOY_DEFAULT_DEADZONE) Then WriteLine File,CONF_JOY_DEADZONE$+"="+Str(JOY_DEADZONE)
	If (JOY_PORT<>JOY_DEFAULT_PORT) Then WriteLine File,CONF_JOY_PORT$+"="+Str(JOY_PORT)
	
	If (AUDIO_MUS_VOL<>AUDIO_DEFAULT_MUS_VOL)Then WriteLine File,CONF_VOL_MUS+"="+Str(AUDIO_MUS_VOL)
	
	CloseFile File
End Function

Function WriteLog(Entry$)
	If (DEBUG)
		Local File=OpenFile(LOGPATH)
		If (File)
			SeekFile(File,FileSize(LOGPATH)-1)
			WriteLine File,Entry+Chr(13)+Chr(10)
			CloseFile File
		End If
	End If
End Function

Function ReadConfigLines(File)
	Local sLine$
	While Not(Eof(File))
		sLine=Trim(Lower(ReadLine(File)))
		If (sLine<>"")
			ProcessLine(sLine)
		End If
	Wend
End Function

Function ProcessLine(s_Ini_Line$)
	If (Not(Instr(s_Ini_Line,"=")))
		Return
	End If
		
	If (Instr(s_Ini_Line,"=",Instr(s_Ini_Line,"=")+1))
		s_Ini_Line=Left(s_Ini_Line,(Instr(s_Ini_Line,"=",Instr(s_Ini_Line,"=")+1))-1)
	End If
	Local s_Property$=Replace(Trim(Lower(Left(s_Ini_Line,Instr(s_Ini_Line,"=")-1)))," ","")
	Local s_Value$
	
	If (Right(Trim(s_Ini_Line),1)="=" )
		s_Property=Trim(Lower(s_Ini_Line))
		s_Value="0"
	Else
		s_Value$=Right(s_Ini_Line,Len(s_Ini_Line)-Instr(s_Ini_Line,"="))
	End If
	
	Select(s_Property)
		Case Trim(Lower(Replace(CONF_CONTROL_METHOD," ",""))):
			CURRENT_CONTROL_METHOD=(Int(s_Value)<>False)
		Case Trim(Lower(Replace(CONF_KEY_LEFT," ",""))):
			CTRL_KEY_LEFT=Int(s_Value)
		Case Trim(Lower(Replace(CONF_KEY_RIGHT," ",""))):
			CTRL_KEY_RIGHT=Int(s_Value)
		Case Trim(Lower(Replace(CONF_KEY_LAUNCH," ",""))):
			CTRL_KEY_LAUNCH=Int(s_Value)
		Case Trim(Lower(Replace(CONF_JOY_PORT," ",""))):
			JOY_PORT=Int(s_Value)
		Case Trim(Lower(Replace(CONF_JOY_LAUNCH," ",""))):
			CTRL_JOY_LAUNCH=Int(s_Value)
		Case Trim(Lower(Replace(CONF_JOY_DEADZONE," ",""))):
			JOY_DEADZONE=Float(s_Value)	
		Case Trim(Lower(Replace(CONF_VOL_MUS," ",""))):
			AUDIO_MUS_VOL#=Float(s_Value)	
			
		Default:
	End Select
End Function
;~IDEal Editor Parameters:
;~F#16#26#2F#3D#53#5E#68
;~C#Blitz3D