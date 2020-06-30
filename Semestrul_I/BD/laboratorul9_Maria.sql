

1.Afișați numele studenților care iau cea mai mare bursa acordată.
SELECT nume from studenti WHERE bursa=(SELECT MAX(bursa) from studenti);

2.Afișați numele studenților care sunt colegi cu un student pe nume Arhire (coleg = același an si aceeași grupă).
	SELECT nume from studenti WHERE (an,grupa) IN (SELECT an,grupa from studenti WHERE nume='Arhire') AND nume<>'Arhire';
	
3.Pentru fiecare grupă afișați numele studenților care au obținut cea mai mică notă la nivelul grupei.

SELECT nume from studenti s JOin note n On n.nr_matricol=s.nr_matricol where (valoare,an,grupa) IN 
(SELECT min(valoare),an,grupa from studenti s join note n On n.nr_matricol=s.nr_matricol GROUP BY an,grupa ) ;

4.Identificați studenții a căror medie este mai mare decât media tuturor notelor din baza de date. Afișați numele și media acestora.

SELECT s.nume,AVG(n.valoare) from studenti s join note n on n.nr_matricol=s.nr_matricol GROUP BY s.nume ,s.nr_matricol 
HAVING AVG(n.valoare)>(SELECT AVG(n.valoare) from note n);

5.Afișați numele și media primelor trei studenți ordonați descrescător după medie.
	SELECT s.nume,AVG(n.valoare) from studenti s JOIN note n On n.nr_matricol=s.nr_matricol GROUP BY s.nume,s.nr_matricol 
	
	SELECT * FROM ( SELECT s.nume,AVG(n.valoare) from studenti s JOIN note n On n.nr_matricol=s.nr_matricol  
	group by s.nume,s.nr_matricol order by 2 desc ) where rownum<4;
	
	
RANK 
5"". SELECT * from (SELECT s.nume,AVG(n.valoare) ,RANK () OVER(ORDER BY AVG(n.valoare) DESC )as pozitie 
from studenti s JOIN note n On n.nr_matricol=s.nr_matricol GROUP BY s.nume,s.nr_matricol) WHERE pozitie<4;


