-module(compare).
-author("Piotr").

%% API
-export([price_compare/1]).


price_compare(Name) ->
  spawn_link(price_checker, check_price, [self()]),
  spawn_link(price_checker, check_price, [self()]),
  erlang:start_timer(300, self(), []),
  loop([], Name).

loop(Results, Name) ->
  receive
    {price, Price} when length(Results) == 1 ->
      global:send(Name, {reply, lists:min(Results++[Price])}),
      compare_server:deleteMe(self());
    {price, Price} ->
      loop(Results++[Price], Name);
    {timeout, _, _} ->
      case length(Results) of
        0 -> io:format("Timeouted request for PID: ~w~n", [self()]),
          global:send(Name, {nodata, "No data aviable for this product.~n"});
        _ -> global:send(Name, {reply, lists:min(Results)})
      end,
      compare_server:deleteMe(self())
  end.
