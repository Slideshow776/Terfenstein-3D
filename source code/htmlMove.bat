RMDIR /S /Q ..\assets
RMDIR /S /Q ..\html
RMDIR /S /Q ..\WEB-INF
del /F /Q ..\index.html
del /F /Q ..\styles.css

TIMEOUT 2

MOVE /Y .\html\build\dist\assets ..\
MOVE /Y .\html\build\dist\html ..\
MOVE /Y .\html\build\dist\WEB-INF ..\
MOVE /Y .\html\build\dist\index.html ..\
MOVE /Y .\html\build\dist\styles.css ..\
