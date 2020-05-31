-module(compare).
-author("Piotr").

%% API
-export([price_compare/2]).


price_compare(ProductName, Pid) ->
  spawn(price_checker, check_price, [self()]),
  spawn(price_checker, check_price, [self()]),
  loop({[], Pid}).

loop({Results, Pid}) ->
  receive
    {price, Price} when size(Results) == 1 ->
      Pid ! lists:min(Results++[Price]),
      compare_server:deleteMe(self());
    {price, Price} ->
      loop({Results++[Price], Pid})
  after
    300 ->
      case size(Results) of
        0 -> Pid ! "No data aviable for this product price.";
        _ -> Pid ! lists:min(Results)
      end,
      compare_server:deleteMe(self())
  end.
