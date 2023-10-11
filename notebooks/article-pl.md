### Wstęp

Garbage Collector jest szarą eminencją każdej aplikacji java.  Mówiąc najogólniej, jest to proces działający w przestrzeni jvm, któregego zadaniem jest zarządzanie pamięcią. W rzeczywistości zarządza on cyklem życia wszystkich obiektów, które zostały powstają w kodzie aplikacji. Tworzy je, alokuje dla nich pamięć, monitoruje, przenosi pomiędzy różnymi obszarami, zarządza referencjami pomiędzy nimi, a także potrafi stwierdzić, że stały się zbędne i skutecznie je usunąć. Cały ten proces o krytycznym znaczeniu dla aplikacji jest w pełni zautomatyzowany i działa w tle poza kontrolą programistów. Czy to znaczy, że możemy całkowicie o nim zapomnieć i zdać się na jego niezawodność ?
Odpowiedź na to pytanie może być tylko jedna - oczywiście że nie, chociażby ze względu na fakt, że mechanizm ten współdzieli zasoby z naszą aplikacją. W pewnych warunkach, podczas nieoptymalnej pracy może stać się dla niej "głośnym sąsiadem". 

Może się zdażyć, że wątki Garbage Collectora zużywają zbyt dużo zasobów, a wtedy zaczyna ich brakować dla wątków realizujących logikę biznesową. Cała aplikacja zaczyna cierpieć z powodu przedłużających się pauz lub nadmiernego zużycia pamięci. A to jest przyczyną spadku wydajności. 

Zwykle jest to moment, gdy my, jako programiści zmuszeni jesteśmy opuścić własną strefę komfortu i włączyć się do 
akcji, dokonać diagnozy i zaaplikować skuteczne poprawki. Poszukiwanie przyczyn tego rodzaju problemów to proces 
trudny i czasochłonny. 

Wymaga wiedzy na temat działania wirtualnej maszyny, znajomości modelu 
pamięciowego jvm oraz sposobu działania samego Garbage Collectora.  Na szczęście nie pozostajemy z tym sami, ponieważ dysponujemy 
narzędziami, które mogą nam w tym pomóc. 

W dzisiejszym artykule spróbuję pokazać, w jaki sposób sami możemy stworzyć takie narzędzie.

### GC

Zanim zaczniemy jednak pisać kod, przypomnijmy sobie kilka najważniejszych informacji o sposobie działania GC. 

Bazuje on na koncepcji zwanej hipotezą generacyjną. Zakłada ona, że najszybciej usuwane są obiekty najmłodsze. Im obiekt jest starszy, tym większe są szanse, że jest on potrzebny. Aby skutecznie zarządzać pulą obiektów, należy uszeregować je na podstawie wieku (liczonego w cyklach pracy GC, które udało się przetrwać danemu obiektowi).

Zgodnie z tymi założeniami,  pamięć została podzielona na dwa regiony- nazywane generacjami (młodą i starą). Młoda generacja jest podzielona na kilka wewnętrznych obszarów:
* Eden
* Survivor 0
* Survivor 1

Cykl życia obiektów rozpoczyna się w przestrzeni Eden. Tam są one tworzone. Następnie Garbage Collector analizuje relacje pomiędzy obiektami i wyszukuje te które są potrzebne. Następnie przenosi je do obszaru o większej dojrzałości, czyli Survivor 0. Obiekty niepotrzebne są usuwane. Analogiczne działania podejmowane są w pozostałych obszarach. W ten sposób obiekty starsze (a więc te które przeżyły więcej iteracji) wędrują w kierunku obszarów dojrzalszych. Finalnie, gdy dotrą one do końca przestrzeni young, nagradzane są promocją do obszaru generacji old. 

<img style="margin-right:20px;" src="gcgeneration.png"> 
<div style="clear:both"></div>

Każdą gemerację GC sprząta w oddzielnych, cyklach które nazywają się  kolekcjami. 

Niektóre kolekcje  wymagają szczególnej uwagi, ponieważ wymagają zatrzymania wątków- nazywamy je pauzami GC (lub fazami stop-the-world). Podczas ich trwania cała aplikacja jest zatrzymywana i nie wykonuje ona żadnych innych zadań. Chciałbym jeszcze raz podkreślić ten fakt, bo ma on ogromne znaczenie.  Podczas faz stop-the-world nasza aplikacja staje się nieresponsywna, dlatego chcielibyśmy, by były one jak najkrótsze i zużywały jak  najmniej zasobów. 

W niektórych sytuacjach, podczas wadliwej pracy GC, pauzy aplikacji mogą zdarzać się zbyt często lub trwać zbyt długo. W rezultacie aplikacja dramatycznie zwalnia co skutkuje wystąpieniem problemów wydajnościowych. 

Możemy zadać sobie pytanie, co my jako programiści możemy zrobić, by zminimalizować szanse wystąpienia tego rodzaju problemów. Okazuje się, że całkiem sporo. Przede wszystkim musimy umieć odpowiedzieć sobie na pytanie czy nasz GC działa w sposób prawidłowy.

Spróbujmy odpowiedzieć sobie na pytanie jak to zrobić.

### GC Logs
Potężnym narzędziem, które mamy w swoim arsenale, jest dziennik GC. Jest to plik tekstowy zawierający szczegółowy zapis operacji, które wykonuje Garbage Collector. 
















  

