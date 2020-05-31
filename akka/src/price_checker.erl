-module(price_checker).
-author("Piotr").

%% API
-export([check_price/1]).

check_price(Pid) ->
  Price = rand:uniform(10),
  SleepTime = rand:uniform(401)+99, % generate number in range <1,401> and add 99 to get number in range <100,500>
  timer:sleep(SleepTime),
  Pid ! {price, Price},
  io:format("Found price ~w, for PID: ~w~n", [Price, Pid]),
  exit(normal).