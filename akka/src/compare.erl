-module(compare).
-author("Piotr").

%% API
-export([price_compare/2]).


price_compare(ProductName, ClientName) ->
  spawn_link(price_checker, check_price, [self()]),
  spawn_link(price_checker, check_price, [self()]),
  spawn_link(db_crawler, getOccur, [ProductName, self()]),
  erlang:start_timer(300, self(), []),
  loop([], ClientName, -1).

loop(Results, Name, Occurrence) ->
  receive
    {db, Occ} ->
      loop(Results, Name, Occ);

    {price, Price} when length(Results) == 1 ->
      case Occurrence >= 0 of
       true -> global:send(Name, {reply, lists:min(Results++[Price]), Occurrence});

        _ -> global:send(Name, {reply, lists:min(Results++[Price])}),
          io:format("DB read took too long time~n")
      end,
      compare_server:deleteMe(self());

    {price, Price} ->
      loop(Results++[Price], Name, Occurrence);

    {timeout, _, _} ->
      case length(Results) of
        0 -> io:format("Timeouted request for PID: ~w~n", [self()]),
          global:send(Name, {nodata, "No data aviable for this product.~n"});

        _ -> case Occurrence >= 0 of
               true -> global:send(Name, {reply, lists:min(Results), Occurrence});

               _ -> global:send(Name, {reply, lists:min(Results)}),
                 io:format("DB read took too long time~n")
             end
      end,
      compare_server:deleteMe(self())
  end.
