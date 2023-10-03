## Wstęp


Garbage Collector należy do najbardziej krytycznych komponentów JVM.   
Jego głównym zadaniem jest automatyczny proces alokacji i zwalniania pamięci. Ponieważ to trudne i wymagające 
zadanie przeprowadzone jest bez jakiegokolwiek udziału programisty, bardzo często zapominamy o tym, że taki proces w 
ogóle się odbywa. Niestety może okazać się to dosyć ryzykowne, o ile uświadomimy sobie jeden podstawowy fakt. 
Mechanizm ten współdzieli zasoby z naszą aplikacją. W pewnych określonych warunkach, podczas nieoptymalnej pracy 
może stać się on "głośnym sąsiadem" dla kodu naszej własnej aplikacji. Gdy wątki Garbage Collectora zużywają zbyt 
dużo zasobów, zaczyna ich brakować dla wątków realizujących logikę biznesową. Wtedy cała aplikacja zaczyna cierpieć 
z powodu przedłużających się pauz lub nadmiernego zużycia pamięci. A to jest przyczyną zauważalnego spadku wydajności. 
Zwykle jest to moment, gdy my, jako programiści zmuszeni jesteśmy opuścić własną strefę komfortu i włączyć się do 
akcji, dokonać diagnozy i zaaplikować skuteczne poprawki. Poszukiwanie przyczyn tego rodzaju problemów to proces 
trudny i czasochłonny. Wymaga wiedzy na temat działania wirtualnej maszyny, modelu 
pamięciowego oraz samego Garbage Collectora.  Na szczęście nie pozostajemy z tym sami, ponieważ dysponujemy 
narzędziami, które mogą nam w tym pomóc. 

W dzisiejszym artykule spróbuję pokazać, w jaki sposób sami możemy stworzyć takie narzędzie.

Zanim zaczniemy jednak pisać kod, przypomnijmy sobie kilka najważniejszych informacji o naszym bohaterze.

Bazuje on na koncepcji zwanej Hipotezą Generacyjną. Zakłada ona, że najszybciej niepotrzebne stają się obiekty 
najmłodsze. Im obiekt jest starszy, tym większe są szanse, że jest on potrzebny. Aby skutecznie zarządzać 
obiektami, należy uszeregować je na podstawie ich starszeństwa.

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










  

