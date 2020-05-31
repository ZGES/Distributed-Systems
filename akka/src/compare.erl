-module(compare).
-author("Piotr").

%% API
-export([price_compare/2]).


price_compare(ProductName, Pid) ->
  spawn_link(price_checker, check_price, [self()]),
  spawn_link(price_checker, check_price, [self()]),
  loop({[], Pid}).

loop({Results, Pid}) ->
  receive
    {price, Price} when length(Results) == 1 ->
      Pid ! {reply, lists:min(Results++[Price])},
      exit(normal);
    {price, Price} ->
      loop({Results++[Price], Pid})
  after
    300 ->
      case length(Results) of
        0 -> Pid ! {nodata, "No data aviable for this product.~n"};
        _ -> Pid ! {reply, lists:min(Results)}
      end,
      exit(normal)
  end.
