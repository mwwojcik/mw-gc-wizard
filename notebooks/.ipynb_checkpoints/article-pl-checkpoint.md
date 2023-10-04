### Wstęp

Garbage Collector jest szarą eminencją każdej aplikacji java.  Mówiąc najogólniej, jest to proces działający w przestrzeni jvm, któregego zadaniem jest zarządzanie pamięcią. W rzeczywistości zarządza on cyklem życia wszystkich obiektów, które zostały powstają w kodzie aplikacji. Tworzy je, alokuje im miejsce, monitoruje, przenosi pomiędzy różnymi obszarami pamięci, zarządza referencjami pomiędzy nimi, a także potrafi stwierdzić, że stały się zbędne i skutecznie je usunąć. Cały ten proces o krytycznym znaczeniu dla aplikacji jest w pełni zautomatyzowany i działa w tle poza kontrolą programistów. Cecha ta niesie za sobą pewne subtelne ryzyko - łatwo jest zapomnieć że proces ten w ogóle się odbywa. 

A tego robić nie powinniśmy ponieważ mechanizm ten współdzieli zasoby z naszą aplikacją. W pewnych warunkach, podczas nieoptymalnej pracy 
może stać się dla niej "głośnym sąsiadem". 

Gdy wątki Garbage Collectora zużywają zbyt 
dużo zasobów, zaczyna ich brakować dla wątków realizujących logikę biznesową. Wtedy cała aplikacja zaczyna cierpieć 
z powodu przedłużających się pauz lub nadmiernego zużycia pamięci. A to jest przyczyną spadku wydajności. 

Zwykle jest to moment, gdy my, jako programiści zmuszeni jesteśmy opuścić własną strefę komfortu i włączyć się do 
akcji, dokonać diagnozy i zaaplikować skuteczne poprawki. Poszukiwanie przyczyn tego rodzaju problemów to proces 
trudny i czasochłonny. 

Wymaga wiedzy na temat działania wirtualnej maszyny, modelu 
pamięciowego oraz samego Garbage Collectora.  Na szczęście nie pozostajemy z tym sami, ponieważ dysponujemy 
narzędziami, które mogą nam w tym pomóc. 

W dzisiejszym artykule spróbuję pokazać, w jaki sposób sami możemy stworzyć takie narzędzie.

### GC

Zanim zaczniemy jednak pisać kod, przypomnijmy sobie kilka najważniejszych informacji o sposobie działania GC. 

Bazuje on na koncepcji zwanej hipotezą generacyjną. Zakłada ona, że najszybciej niepotrzebne stają się obiekty, które są najmłodsze. Im obiekt jest starszy, tym większe są szanse, że jest on potrzebny. Aby skutecznie zarządzać pulą obiektów, należy uszeregować je na podstawie wieku (liczonego w cyklach pracy GC, które udało się przetrwać danemu obiektowi).

Zgodnie z tymi założeniami,  pamięć została podzielona na dwa regiony- nazywane generacjami (młodą i starą). Młoda generacja jest podzielona na kilka wewnętrznych obszarów:
* Eden
* Survivor 0
* Survivor 1

Cykl życia obiektów rozpoczyna się w przestrzeni Eden. Tam są one tworzone. Następnie Garbage Collector analizuje relacje pomiędzy obiektami i wyszukuje te które są potrzebne. Następnie przenosi je do obszaru o większej dojrzałości, czyli Survivor 0. Obiekty niepotrzebne są usuwane. Analogiczne działania podejmowane są w pozostałych obszarach. W ten sposób obiekty dojrzałe (a więc te które przeżyły więcej iteracji) wędrują w kierunku obszarów przechowujących obiekty o większej dojrzałości. Finalnie, obiekty najstarsze nagradzane są promocją do obszaru gemeracko old. 

<img style="margin-right:20px;" src="gcgeneration.png"> 
<div style="clear:both"></div>

Każdą gemerację można sprzątać w oddzielnych, cyklach które nazywają się  kolekcjami. Chcielibyśmy, by były one jak najkrótsze i aby do ich przeprowadzenia potrzebne było jak najmniej zasobów. 

Niektóre kolekcje  wymagają szczególnej uwagi, ponieważ wymagają zatrzymania wątków- nazywamy to pauzami GC (lub fazą stop-the-world). Podczas tych pauz cała aplikacja jest zatrzymywana i nie może wykonywać swoich zadań biznesowych. 

W niektórych sytuacjach, podczas wadliwej pracy GC, pauzy aplikacji mogą zdarzać się zbyt często lub trwać zbyt długo. W rezultacie aplikacja dramatycznie zwalnia co skutkuje wystąpieniem problemów wydajnościowych. 

Możemy zadać sobie pytanie, co my jako programiści możemy zrobić, by zminimalizować szanse wystąpienia tego rodzaju problemów. Okazuje się, że całkiem sporo. Przede wszystkim musimy umieć odpowiedzieć sobie na pytanie czy nasz GC działa w sposób prawidłowy. 

### GC Logs
Potężnym narzędziem, które mamy w swoim arsenale, jest dziennik GC. Jest to plik tekstowy zawierający szczegółowy zapis operacji, które wykonuje Garbage Collector.
















  

