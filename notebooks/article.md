## Wstęp

Garbage Collector to cichy bohater każdej aplikacji java. Zamknięty wewnątrz czarnego pudła z etykieta "jvm" wykonuje ciężką pracę, której nikt inny wykonywać nie chce.  Chwalimy go rzadko, bo  gdy radzi sobie świetnie, jest niewidoczny. Narzekamy, gdy jego nieprawidłowa praca skutkuje nadmiernym zużyciem pamięci lub niespodziewanymi pauzami aplikacji. Poszukiwanie przyczyn to proces trudny i czasochłonny. Wymaga wiedzy na temat działania wirtualnej maszyny, modelu pamięciowego oraz specyfiki działania samego Garbage Collectora. Na szczęście nie pozostajemy z tym sami, dysponujemy narzędziami, które mogą nam w tym pomóc. W dzisiejszym artykule opiszę w jaki sposób sami możemy stworzyć takie narzędzie.

Zanim zaczniemy jednak pisać kod, przypomnijmy sobie kilka najważniejszych informacji o naszym bohaterze.

Garbage Collector to proces JVM specjalizujący się w przydzielaniu i zwalnianiu pamięci. 

Bazuje on na koncepcji zwanej Hipotezą Generacyjną. Zakłada ona, że najszybciej niepotrzebne stają się obiekty najmłodsze. Im obiekt jest starszy, tym większe są szanse że jest on potrzebny.
Zgodnie z tymi założeniami,  pamięć została podzielona na regiony- nazywane generacjami (młodą i starą). Młoda generacja jest podzielona na kilka wewnętrznych obszarów, nazywanych Eden, Survivor 0, Survivor 1. 

W każdym z tych obszarów przechowywane są obiekty o określonej dojrzałości. 

Cykl życia obiektów rozpoczyna się w przestrzeni Eden. Tam są one tworzone. Po wzbudzeniu, Garbage Collector analizuje relacje pomiędzy obiektami i wyszukuje te które są użyteczne (wykorzystuje przy tym algorytmy skalarne i wektorowe, których opis jest poza scopem tego artykułu), a następnie przenosi je do obszaru o większej dojrzałości, czyli Survivor 0. Obiekty niepotrzebne są usuwane. Analogiczne działania podejmowane są w pozostałych obszarach. W ten sposób obiekty dojrzałe (a więc te które przeżyły więcej iteracji) umieszczane są w obszarach o coraz większej dojrzałości. Finalnie, obiekty najstarsze nagradzane są promocją do obszaru old.

<img style="margin-right:20px;" src="gcgeneration.png"> 
<div style="clear:both"></div>

Każdą generację można sprzątać w oddzielnych, cyklach które nazywają się  kolekcjami. Chcielibyśmy, by były one jak najkrótsze i aby do ich przeprowadzenia potrzebne było jak najmniej zasobów. 

Niektóre kolekcje  wymagają szczególnej uwagi, ponieważ wymagają zatrzymania wątków- nazywamy to pauzami GC (lub fazą stop-the-world). Podczas tych pauz cała aplikacja jest zatrzymywana i nie może wykonywać swoich zadań biznesowych. 

W niektórych sytuacjach, podczas wadliwej pracy GC, pauzy aplikacji mogą zdarzać się zbyt często lub trwać zbyt długo. W rezultacie aplikacja dramatycznie zwalnia co skutkuje wystąpieniem problemów wydajnościowych. 

Możemy zadać sobie pytanie, co my jako programiści możemy zrobić, by zminimalizować szanse wystąpienia tego rodzaju problemów. Okazuje się, że całkiem sporo. Przede wszystkim musimy umieć odpowiedzieć sobie na pytanie czy nasz GC działa w sposób prawidłowy. 

Potężnym narzędziem, które mamy w swoim arsenale, jest dziennik GC. Jest to plik tekstowy zawierający szczegółowy zapis operacji, które wykonuje Garbage Collector.






In the case of G1 GC, we can distinguish between four main types collection cycles:

  <table style="align:left;border:1px solid gray; text-align:left; margin-right:auto;margin-left:0px">
    <tr>
        <td width="50%">young collection cycle</td>
        <td>During this collection, GC pauses the application threads to move live objects from the young regions into survivor regions or promote them into old regions</td>
    </tr>       
       <tr>
        <td width="50%">mixed collection cycle</td>
        <td>During this collection  the steps of the young collection are carried out and, in addition G1 GC additionaly moves live objects from some old regions to free ones, which become a part of the old generation. Single mixed collection is similar to a young collection pause. Sometimes, there could be more tha one mixed collection pause. This is called a mixed collection cycle.</td>
    </tr>
     <tr>
        <td width="50%">multistage concurrent marking cycle</td>
        <td>This collection is run, when the old generation occupancy reaches the threshold. During this stage G1 GC calculates the amount of live objects per old region, and ranks the old regions.</td>
     </tr>
    <tr>
        <td width="50%">full garbage collection pause</td>
        <td>  </td>
    </tr>
</table>

**Tip:** You can find a detailed description of how the GC works in the book [Java® Performance Companion](https://www.oreilly.com/library/view/java-performance-companion/9780133796896)

<div style="clear:both"></div>










  

