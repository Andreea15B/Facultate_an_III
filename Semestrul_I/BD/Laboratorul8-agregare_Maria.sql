Functii de agregare(grupare)
COUNT
MIN ---number
MAX---char(varchar2)
-----------
SUM    |
AVG    | number
STDDEN |

GROUP BY  (de atribute libere,care nu apar in functiile de agregare)
HAVING 

-----EXERCITII---
1.Afișați numărul de studenți din fiecare an.
	SELECT an,COUNT(nr_matricol) FROM studenti GROUP BY an ORDER BY 2 DESC;
	
2.Afișați numărul de studenți din fiecare grupă a fiecărui an de studiu. Ordonați crescător după anul de studiu și după grupă.
	SELECT an ,grupa,COUNT(nr_matricol) FROM studenti GROUP  BY an ,grupa ORDER BY 1,2 ASC;

		
3.Afișați numărul de studenți din fiecare grupă a fiecărui an de studiu și specificați câți dintre aceștia sunt bursieri.
	SELECT an,grupa ,COUNT(nr_matricol),COUNT(bursa) FROM studenti  GROUP BY an,grupa ;


4.Afișați suma totală cheltuită de facultate pentru acordarea burselor.
	SELECT SUM(bursa) from studenti;
  
5.Afișați valoarea bursei/cap de student (se consideră că studentii care nu sunt bursieri primesc 0 RON); altfel spus:	
		cât se cheltuiește în medie pentru un student?
	SELECT ROUND(SUM(nvl(bursa,0))/COUNT(nr_matricol)) FROM studenti;
		
6.Afișați numărul de note de fiecare fel (câte note de 10, câte de 9,etc.). Ordonați descrescător după valoarea notei.
	SELECT valoare,COUNT(valoare) FROM note GROUP BY valoare ORDER BY valoare DESC;
	
9.Afișați pentru fiecare elev care are măcar o notă, numele și media notelor sale. Ordonați descrescător după valoarea mediei.
	SELECT s.nume,ROUND(AVG(n.valoare)) AS "Media" FROM studenti s JOIN note n ON n.nr_matricol=s.nr_matricol
		GROUP BY s.nume  HAVING count(n.valoare)>0 ORDER BY ROUND(AVG(n.valoare)) DESC;

7.Afișați numărul de note pus în fiecare zi a săptămânii. Ordonați descrescător după numărul de note.
	SELECT TO_CHAR(data_notare,'Day'), COUNT(valoare) FROM note GROUP BY TO_CHAR(data_notare,'Day') order by COUNT(valoare) DESC;
	
8.Afișați numărul de note pus în fiecare zi a săptămânii. Ordonați crescător după ziua saptamanii: Sunday, Monday, etc.
	SELECT TO_CHAR(data_notare,'Day'), TO_CHAR(data_notare,'d'),COUNT(valoare) FROM note
	GROUP BY TO_CHAR(data_notare,'Day'), TO_CHAR(data_notare,'d') ORDER BY TO_CHAR(data_notare,'d') ASC;

10.Modificați interogarea anterioară(de la 9) pentru a afișa și elevii fără nici o notă. Media acestora va fi null.
SELECT s.nume,ROUND(AVG(n.valoare)) AS "Media" FROM studenti s LEFT JOIN note n ON n.nr_matricol=s.nr_matricol
		GROUP BY s.nume  ORDER BY ROUND(AVG(n.valoare)) DESC;
		
11.Modificați interogarea anterioară pentru a afișa pentru elevii fără nici o notă media 0.
	SELECT s.nume,NVL(ROUND(AVG(n.valoare)),0) AS "Media" FROM studenti s LEFT JOIN note n ON n.nr_matricol=s.nr_matricol
		GROUP BY s.nume  ORDER BY ROUND(AVG(n.valoare)) DESC;
		
12.	Modificati interogarea de mai sus pentru a afisa doar studentii cu media mai mare ca 8.
		SELECT s.nume,ROUND(AVG(n.valoare)) AS "Media" FROM studenti s  JOIN note n ON n.nr_matricol=s.nr_matricol
		GROUP BY s.nume HAVING AVG(n.valoare)>8 ORDER BY ROUND(AVG(n.valoare))  DESC;

13.Afișați numele, cea mai mare notă, cea mai mică notă și media doar pentru acei studenti care au cea mai mică notă mai mare sau egală cu 7.
 SELECT s.nume ,MAX(n.valoare),MIN(n.valoare),AVG(n.valoare) FROM studenti  s JOIN note n ON n.nr_matricol=s.nr_matricol 
	GROUP BY s.nume ,s.nr_matricol HAVING MIN(n.valoare)>=7;
					(sunt studenti cu acelasi nume)

14.Afișați numele și mediile studenților care au cel puțin un număr de 4 note puse în catalog.	
	SELECT s.nume ,AVG(n.valoare) from studenti s JOIN note n ON n.nr_matricol=s.nr_matricol GROUP BY s.nume,s.nr_matricol HAVING COUNT(n.valoare)>=4;	
	
15.Afișați numele și mediile studenților din grupa A2 anul 3.
  SELECT s.nume ,AVG(n.valoare) FROM studenti  s JOIN note n ON n.nr_matricol=s.nr_matricol WHERE s.an=3 AND s.grupa LIKE 'A2' GROUP BY s.nume ,s.nr_matricol;

16.Afișați cea mai mare medie obținută de vreun student.
 SELECT MAX(AVG(valoare)) from note GROUP BY nr_matricol;
 
 ----Afisam si studentul --
 --daca avem dubla agregare nu trebuie sa avem variare libere --
 SELECT s.nume ,AVG(n.valoare) from studenti s JOIN note n On n.nr_matricol=s.nr_matricol GROUP BY s.nume,s.nr_matricol
	having AVG(n.valoare)=(SELECT MAX(AVG(valoare)) from note GROUP BY nr_matricol);
  
  
  
  
  