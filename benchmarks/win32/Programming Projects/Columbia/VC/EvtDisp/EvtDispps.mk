
EvtDispps.dll: dlldata.obj EvtDisp_p.obj EvtDisp_i.obj
	link /dll /out:EvtDispps.dll /def:EvtDispps.def /entry:DllMain dlldata.obj EvtDisp_p.obj EvtDisp_i.obj \
		kernel32.lib rpcndr.lib rpcns4.lib rpcrt4.lib oleaut32.lib uuid.lib \

.c.obj:
	cl /c /Ox /DWIN32 /D_WIN32_WINNT=0x0500 /DREGISTER_PROXY_DLL \
		$<

clean:
	@del EvtDispps.dll
	@del EvtDispps.lib
	@del EvtDispps.exp
	@del dlldata.obj
	@del EvtDisp_p.obj
	@del EvtDisp_i.obj
