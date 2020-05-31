-module(client).
-author("Piotr").

%% API
-export([start/1]).


start(Name) ->
  spawn(fun() -> init(Name) end).

stop(Name) ->
  global:unregister_name(Name),
  io:format("You are successfuly unregistered~n"),
  ok.

init(Name) ->
  global:register_name(Name, self()),
  timer:sleep(1),
  loop(Name).

loop(Name) ->
  {ok, ProductName} = io:read("Enter product name: "),
  case ProductName == stop of
    true -> stop(Name);

    _ -> compare_server:checkPrice(ProductName, Name)
  end,

  receive
    {nodata, Info} ->
      io:format(Info),
      timer:sleep(1),
      loop(Name);

    {reply, Price} ->
      io:format("The best price found is ~B~n", [Price]),
      timer:sleep(1),
      loop(Name);

    {reply, Price, Occurrence} ->
      io:format("The best price found is ~B. There were ~B same queries~n", [Price, Occurrence]),
      timer:sleep(1),
      loop(Name)
  end.