%%%-------------------------------------------------------------------
%%% @author Piotr
%%% @copyright (C) 2020, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 25. maj 2020 11:12
%%%-------------------------------------------------------------------
-module('Server').
-behaviour(gen_server).
-author("Piotr").

%% API
-export([init/1, handle_call/3, handle_cast/2]).

init(_Arg0) ->
erlang:error(not_implemented).

handle_call(_Arg0, _Arg1, _Arg2) ->
  erlang:error(not_implemented).

handle_cast(_Arg0, _Arg1) ->
  erlang:error(not_implemented).