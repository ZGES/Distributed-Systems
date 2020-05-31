-module(client).
-author("Piotr").

%% API
-export([start/1]).


start(Name) ->
  spawn(fun() -> init(Name) end).

stop(Name) ->
  global:unregister_name(Name),
  ok.

init(Name) ->
  global:register_name(Name, self()),
  timer:sleep(1),
  loop(Name).

loop(Name) ->
  {ok, ProductName} = io:read("Enter product name: "),
  if
    ProductName == stop ->
      stop(Name);
    true ->
      compare_server:checkPrice(ProductName, Name)
  end,
  receive
    {nodata, Info} ->
      io:format(Info),
      timer:sleep(1),
      loop(Name);
    {reply, Price} ->
      io:format("The best price found is ~B~n", [Price]),
      timer:sleep(1),
      loop(Name)
  end.