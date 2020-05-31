-module(compare).
-author("Piotr").

%% API
-export([price_compare/2]).


price_compare(ProductName, Name) ->
  spawn_link(price_checker, check_price, [self()]),
  spawn_link(price_checker, check_price, [self()]),
  loop({[], Name}).

loop({Results, Name}) ->
  receive
    {price, Price} when length(Results) == 1 ->
      global:send(Name, {reply, lists:min(Results++[Price])}),
      compare_server:deleteMe(self());
    {price, Price} ->
      loop({Results++[Price], Name})
  after
    300 ->
      case length(Results) of
        0 -> global:send(Name, {nodata, "No data aviable for this product.~n"});
        _ -> global:send(Name, {reply, lists:min(Results)})
      end,
      compare_server:deleteMe(self())
  end.
