-module(client).
-author("Piotr").

%% API
-export([start/0]).


start() ->
  spawn(fun() -> init() end).

stop() ->
  ok.

init() ->
  timer:sleep(1),
  loop().

loop() ->
  {ok, ProductName} = io:read("Enter product name: "),
  if
    ProductName == stop ->
      stop();
    true ->
      compare_server:checkPrice(ProductName, self())
  end,
  receive
    {nodata, Info} ->
      io:format(Info),
      timer:sleep(1),
      loop();
    {reply, Price} ->
      io:format("The best price found is ~B~n", [Price]),
      timer:sleep(1),
      loop()
  end.