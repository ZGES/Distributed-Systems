-module(db_crawler).
-author("Piotr").

%% API
-export([getOccur/2]).

-record(queries, {productName, occurrences}).


getOccur(ProductName, Pid) ->
  Func = fun() ->
    case mnesia:read({queries, ProductName}) of
      [] -> mnesia:write(#queries{productName = ProductName, occurrences = 1}),
        Pid ! {db, 0};

      [#queries{occurrences = Occ}] -> mnesia:write(#queries{productName = ProductName, occurrences = Occ+1}),
        Pid ! {db, Occ}
    end
  end,
  io:format("DB updated~n"),
  mnesia:activity(transaction, Func).
