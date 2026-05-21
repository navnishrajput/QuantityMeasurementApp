import React, { useState, useEffect } from 'react';
import { quantityService } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { 
  Ruler, 
  Droplet, 
  Weight as WeightIcon, 
  Thermometer, 
  ArrowLeftRight, 
  Calculator, 
  RefreshCw, 
  AlertTriangle,
  Sparkles,
  Info
} from 'lucide-react';

const CATEGORIES = {
  LENGTH: {
    name: 'Length',
    icon: Ruler,
    description: 'Feet, Inches, Yards, Centimeters',
    units: ['FEET', 'INCH', 'YARDS', 'CENTIMETERS'],
    color: 'from-blue-500/20 to-indigo-500/10 hover:border-blue-500/40 text-blue-400',
    borderColor: 'border-blue-500/20'
  },
  VOLUME: {
    name: 'Volume',
    icon: Droplet,
    description: 'Litres, Millilitres, Gallons',
    units: ['LITRE', 'MILLILITRE', 'GALLON'],
    color: 'from-cyan-500/20 to-teal-500/10 hover:border-cyan-500/40 text-cyan-400',
    borderColor: 'border-cyan-500/20'
  },
  WEIGHT: {
    name: 'Weight',
    icon: WeightIcon,
    description: 'Kilograms, Grams, Pounds',
    units: ['KILOGRAM', 'GRAM', 'POUND'],
    color: 'from-purple-500/20 to-pink-500/10 hover:border-purple-500/40 text-purple-400',
    borderColor: 'border-purple-500/20'
  },
  TEMPERATURE: {
    name: 'Temperature',
    icon: Thermometer,
    description: 'Celsius, Fahrenheit, Kelvin',
    units: ['CELSIUS', 'FAHRENHEIT', 'KELVIN'],
    color: 'from-amber-500/20 to-orange-500/10 hover:border-amber-500/40 text-amber-400',
    borderColor: 'border-amber-500/20'
  }
};

export const Dashboard = () => {
  const { isGuest, loginWithGoogle } = useAuth();
  
  // App states
  const [category, setCategory] = useState('LENGTH');
  const [tab, setTab] = useState('CONVERT'); // 'CONVERT', 'COMPARE', 'CALCULATE'
  
  // Inputs
  const [value1, setValue1] = useState('1');
  const [unit1, setUnit1] = useState('FEET');
  const [value2, setValue2] = useState('1');
  const [unit2, setUnit2] = useState('INCH');
  const [operator, setOperator] = useState('ADD'); // 'ADD', 'SUBTRACT', 'DIVIDE'
  const [targetUnit, setTargetUnit] = useState('FEET');
  
  // Output states
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Sync default units when category changes
  useEffect(() => {
    const list = CATEGORIES[category].units;
    setUnit1(list[0]);
    setUnit2(list[1] || list[0]);
    setTargetUnit(list[0]);
    setResult(null);
    setError(null);
  }, [category]);

  // Handle operation calculations
  const calculate = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    setError(null);
    setResult(null);

    const val1Parsed = parseFloat(value1);
    const val2Parsed = parseFloat(value2);

    if (isNaN(val1Parsed) || (tab !== 'CONVERT' && isNaN(val2Parsed))) {
      setError("Please specify valid numerical quantities.");
      setLoading(false);
      return;
    }

    try {
      let data;
      if (tab === 'CONVERT') {
        data = await quantityService.convert(val1Parsed, unit1, category, unit2);
      } else if (tab === 'COMPARE') {
        data = await quantityService.compare(val1Parsed, unit1, val2Parsed, unit2, category);
      } else if (tab === 'CALCULATE') {
        if (category === 'TEMPERATURE') {
          throw new Error("Arithmetic operations are not meaningful for absolute temperatures.");
        }
        if (operator === 'ADD') {
          data = await quantityService.add(val1Parsed, unit1, val2Parsed, unit2, category, targetUnit);
        } else if (operator === 'SUBTRACT') {
          data = await quantityService.subtract(val1Parsed, unit1, val2Parsed, unit2, category, targetUnit);
        } else if (operator === 'DIVIDE') {
          data = await quantityService.divide(val1Parsed, unit1, val2Parsed, unit2, category);
        }
      }

      if (data.isError) {
        setError(data.errorMessage || "An error occurred during calculation.");
      } else {
        setResult(data);
      }
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || err.message || "Network calculation failed.");
    } finally {
      setLoading(false);
    }
  };

  // Swap units helper (specifically for simple conversion)
  const swapUnits = () => {
    const temp = unit1;
    setUnit1(unit2);
    setUnit2(temp);
    setValue1(result ? result.resultValue.toFixed(4) : value1);
    setResult(null);
  };

  const currentCat = CATEGORIES[category];

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Page Title & Guest banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-3xl font-extrabold tracking-tight bg-gradient-to-r from-white to-slate-400 bg-clip-text text-transparent">
            Measurement Hub
          </h2>
          <p className="text-slate-400 text-sm mt-1">Convert units, compare magnitudes, and run precision arithmetic.</p>
        </div>

        {isGuest && (
          <div className="glass-panel px-4 py-3 rounded-2xl border border-indigo-500/20 bg-indigo-500/5 flex items-center gap-3 max-w-md animate-pulse">
            <Info className="h-5 w-5 text-indigo-400 flex-shrink-0" />
            <div className="text-xs">
              <span className="font-semibold text-slate-200">Guest Session:</span> persistent logs are disabled.
              <button 
                onClick={loginWithGoogle}
                className="text-indigo-400 hover:text-indigo-300 font-bold ml-1 hover:underline underline-offset-2 flex items-center gap-0.5 inline-flex"
              >
                Sign In <Sparkles className="h-3 w-3 text-amber-500" />
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Category selector grid */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {Object.entries(CATEGORIES).map(([key, item]) => {
          const Icon = item.icon;
          const isActive = category === key;
          return (
            <button
              key={key}
              onClick={() => setCategory(key)}
              className={`
                glass-panel p-5 rounded-2xl text-left border relative overflow-hidden transition-all duration-300 group
                bg-gradient-to-br ${item.color}
                ${isActive ? 'glow-card-active border-indigo-500 bg-indigo-950/20' : 'border-slate-900/60 hover:bg-slate-900/40 hover:-translate-y-1'}
              `}
            >
              {/* Highlight bar */}
              {isActive && (
                <div className="absolute top-0 inset-x-0 h-1 bg-gradient-to-r from-indigo-500 to-violet-500" />
              )}
              <div className="flex justify-between items-start mb-3">
                <div className={`p-2.5 rounded-xl ${isActive ? 'bg-indigo-600/30' : 'bg-slate-900/80'} group-hover:scale-105 duration-200`}>
                  <Icon className="h-6 w-6" />
                </div>
              </div>
              <h3 className="font-bold text-slate-200 text-base">{item.name}</h3>
              <p className="text-[11px] text-slate-500 font-medium truncate mt-0.5">{item.description}</p>
            </button>
          );
        })}
      </div>

      {/* Primary tool canvas grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        
        {/* Left Side: Interactive controls card */}
        <div className="lg:col-span-7 glass-panel p-6 lg:p-8 rounded-3xl border border-slate-900/60 shadow-glass flex flex-col justify-between">
          <div>
            {/* Tool selectors (Tabs) */}
            <div className="flex border-b border-slate-900 pb-px mb-6 gap-2">
              <button
                onClick={() => setTab('CONVERT')}
                className={`pb-3 px-4 text-sm font-semibold border-b-2 transition-all duration-200 flex items-center gap-2 ${
                  tab === 'CONVERT' 
                    ? 'border-indigo-500 text-indigo-400' 
                    : 'border-transparent text-slate-400 hover:text-slate-200'
                }`}
              >
                <RefreshCw className="h-4 w-4" />
                <span>Convert</span>
              </button>
              <button
                onClick={() => setTab('COMPARE')}
                className={`pb-3 px-4 text-sm font-semibold border-b-2 transition-all duration-200 flex items-center gap-2 ${
                  tab === 'COMPARE' 
                    ? 'border-indigo-500 text-indigo-400' 
                    : 'border-transparent text-slate-400 hover:text-slate-200'
                }`}
              >
                <ArrowLeftRight className="h-4 w-4" />
                <span>Compare</span>
              </button>
              <button
                onClick={() => setTab('CALCULATE')}
                disabled={category === 'TEMPERATURE'}
                className={`pb-3 px-4 text-sm font-semibold border-b-2 transition-all duration-200 flex items-center gap-2 ${
                  category === 'TEMPERATURE' ? 'opacity-40 cursor-not-allowed' : ''
                } ${
                  tab === 'CALCULATE' 
                    ? 'border-indigo-500 text-indigo-400' 
                    : 'border-transparent text-slate-400 hover:text-slate-200'
                }`}
              >
                <Calculator className="h-4 w-4" />
                <span>Calculator</span>
              </button>
            </div>

            {/* Main Interactive Form */}
            <form onSubmit={calculate} className="space-y-6">
              {/* Row 1: First Input & Unit */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <label className="text-xs font-semibold text-slate-400 tracking-wide uppercase">Value 1</label>
                  <input
                    type="number"
                    step="any"
                    value={value1}
                    onChange={(e) => { setValue1(e.target.value); setResult(null); }}
                    className="w-full py-3.5 px-4 rounded-xl glass-input font-medium"
                    placeholder="Enter value"
                    required
                  />
                </div>

                <div className="space-y-2">
                  <label className="text-xs font-semibold text-slate-400 tracking-wide uppercase">Unit 1</label>
                  <select
                    value={unit1}
                    onChange={(e) => { setUnit1(e.target.value); setResult(null); }}
                    className="w-full py-3.5 px-4 rounded-xl glass-input font-medium cursor-pointer"
                  >
                    {currentCat.units.map(u => (
                      <option key={u} value={u} className="bg-slate-950 text-slate-200">{u}</option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Conversion Middle element: Swap button */}
              {tab === 'CONVERT' && (
                <div className="flex justify-center py-2">
                  <button
                    type="button"
                    onClick={swapUnits}
                    className="p-2.5 rounded-full bg-slate-900 border border-slate-800 text-indigo-400 hover:bg-slate-800 hover:text-indigo-300 transition-colors shadow-sm"
                  >
                    <ArrowLeftRight className="h-5 w-5 transform rotate-90 md:rotate-0" />
                  </button>
                </div>
              )}

              {/* Calculator Middle element: Operator Selection */}
              {tab === 'CALCULATE' && (
                <div className="space-y-2">
                  <label className="text-xs font-semibold text-slate-400 tracking-wide uppercase">Operator</label>
                  <div className="grid grid-cols-3 gap-2">
                    {['ADD', 'SUBTRACT', 'DIVIDE'].map((op) => (
                      <button
                        key={op}
                        type="button"
                        onClick={() => { setOperator(op); setResult(null); }}
                        className={`
                          py-2.5 rounded-xl border font-bold text-xs transition-all duration-200
                          ${operator === op 
                            ? 'border-indigo-500 bg-indigo-500/10 text-indigo-300' 
                            : 'border-slate-800 bg-slate-900/30 text-slate-400 hover:text-slate-200'}
                        `}
                      >
                        {op}
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {/* Row 2: Second Input & Unit (COMPARE / CALCULATE only) */}
              {tab !== 'CONVERT' && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 animate-fade-in">
                  <div className="space-y-2">
                    <label className="text-xs font-semibold text-slate-400 tracking-wide uppercase">Value 2</label>
                    <input
                      type="number"
                      step="any"
                      value={value2}
                      onChange={(e) => { setValue2(e.target.value); setResult(null); }}
                      className="w-full py-3.5 px-4 rounded-xl glass-input font-medium"
                      placeholder="Enter value"
                      required
                    />
                  </div>

                  <div className="space-y-2">
                    <label className="text-xs font-semibold text-slate-400 tracking-wide uppercase">Unit 2</label>
                    <select
                      value={unit2}
                      onChange={(e) => { setUnit2(e.target.value); setResult(null); }}
                      className="w-full py-3.5 px-4 rounded-xl glass-input font-medium cursor-pointer"
                    >
                      {currentCat.units.map(u => (
                        <option key={u} value={u} className="bg-slate-950 text-slate-200">{u}</option>
                      ))}
                    </select>
                  </div>
                </div>
              )}

              {/* Row 3: Target unit select (CALCULATE only, and not DIVIDE) */}
              {tab === 'CALCULATE' && operator !== 'DIVIDE' && (
                <div className="space-y-2 animate-fade-in">
                  <label className="text-xs font-semibold text-slate-400 tracking-wide uppercase">Target Output Unit (Optional)</label>
                  <select
                    value={targetUnit}
                    onChange={(e) => { setTargetUnit(e.target.value); setResult(null); }}
                    className="w-full py-3.5 px-4 rounded-xl glass-input font-medium cursor-pointer"
                  >
                    {currentCat.units.map(u => (
                      <option key={u} value={u} className="bg-slate-950 text-slate-200">{u}</option>
                    ))}
                  </select>
                </div>
              )}

              {/* Swap target unit for simple conversions */}
              {tab === 'CONVERT' && (
                <div className="space-y-2">
                  <label className="text-xs font-semibold text-slate-400 tracking-wide uppercase">Target Unit</label>
                  <select
                    value={unit2}
                    onChange={(e) => { setUnit2(e.target.value); setResult(null); }}
                    className="w-full py-3.5 px-4 rounded-xl glass-input font-medium cursor-pointer"
                  >
                    {currentCat.units.map(u => (
                      <option key={u} value={u} className="bg-slate-950 text-slate-200">{u}</option>
                    ))}
                  </select>
                </div>
              )}
            </form>
          </div>

          <button
            onClick={() => calculate()}
            disabled={loading}
            className="w-full mt-8 py-4 rounded-xl bg-gradient-to-r from-indigo-600 via-purple-600 to-cyan-600 hover:shadow-glow-indigo text-white font-bold transition-all duration-300 flex items-center justify-center gap-2"
          >
            {loading ? (
              <div className="h-5 w-5 rounded-full border-2 border-white border-t-transparent animate-spin" />
            ) : (
              <span>Compute Solution</span>
            )}
          </button>
        </div>

        {/* Right Side: Elegant Output canvas card */}
        <div className="lg:col-span-5 flex flex-col justify-between gap-6">
          <div className="glass-panel p-6 lg:p-8 rounded-3xl border border-slate-900/60 shadow-glass flex-1 flex flex-col justify-center relative overflow-hidden">
            {/* Visual backdrop ambient glow */}
            <div className="absolute inset-0 bg-gradient-to-br from-indigo-500/5 to-cyan-500/5 opacity-50" />

            {/* 1. Loader screen */}
            {loading && (
              <div className="text-center space-y-4 py-8 relative z-10">
                <div className="inline-block relative h-10 w-10">
                  <div className="absolute inset-0 rounded-full border-3 border-slate-800" />
                  <div className="absolute inset-0 rounded-full border-3 border-indigo-500 border-t-transparent animate-spin" />
                </div>
                <p className="text-slate-400 text-sm font-semibold tracking-wide animate-pulse">Running Backend Logic...</p>
              </div>
            )}

            {/* 2. Error Card */}
            {!loading && error && (
              <div className="p-5 rounded-2xl border border-rose-500/20 bg-rose-500/5 text-center space-y-3 relative z-10 animate-fade-in shadow-glow-rose">
                <AlertTriangle className="h-10 w-10 text-rose-500 mx-auto" />
                <h4 className="font-extrabold text-rose-400 text-sm uppercase tracking-wider">Calculation Error</h4>
                <p className="text-slate-300 text-sm font-medium">{error}</p>
              </div>
            )}

            {/* 3. Successful Result Canvas */}
            {!loading && !error && result && (
              <div className="text-center space-y-6 py-6 relative z-10 animate-fade-in">
                {tab === 'CONVERT' && (
                  <div className="space-y-4">
                    <p className="text-xs font-bold text-indigo-400 uppercase tracking-widest">Conversion Successful</p>
                    <div className="space-y-1">
                      <p className="text-slate-400 text-sm font-bold">{result.inputValue1} {result.inputUnit1} =</p>
                      <h2 className="text-5xl font-black bg-gradient-to-r from-indigo-200 via-slate-100 to-cyan-200 bg-clip-text text-transparent break-words p-2">
                        {result.resultValue % 1 === 0 ? result.resultValue : result.resultValue.toFixed(4)}
                      </h2>
                      <p className="text-slate-300 font-extrabold text-lg uppercase tracking-wide">{result.resultUnit}</p>
                    </div>
                  </div>
                )}

                {tab === 'COMPARE' && (
                  <div className="space-y-4">
                    <p className="text-xs font-bold text-cyan-400 uppercase tracking-widest">Comparison Summary</p>
                    
                    <div className="p-4 rounded-2xl bg-slate-900/50 border border-slate-900 flex justify-around items-center gap-3">
                      <div>
                        <p className="text-xl font-bold text-slate-200">{result.inputValue1}</p>
                        <p className="text-[10px] text-slate-500 font-bold">{result.inputUnit1}</p>
                      </div>
                      <div className="px-3 py-1 bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 rounded-xl font-black text-sm">
                        {result.inputValue1 === result.resultValue ? '=' : result.inputValue1 > result.resultValue ? '>' : '<'}
                      </div>
                      <div>
                        <p className="text-xl font-bold text-slate-200">{result.inputValue2}</p>
                        <p className="text-[10px] text-slate-500 font-bold">{result.inputUnit2}</p>
                      </div>
                    </div>

                    <div className="space-y-1">
                      <p className="text-slate-400 text-xs font-medium">Equalized base unit magnitude:</p>
                      <p className="text-slate-200 font-black text-lg">{result.resultValue % 1 === 0 ? result.resultValue : result.resultValue.toFixed(4)} {result.resultUnit}</p>
                    </div>
                  </div>
                )}

                {tab === 'CALCULATE' && (
                  <div className="space-y-4">
                    <p className="text-xs font-bold text-violet-400 uppercase tracking-widest">Calculation Complete</p>
                    <div className="space-y-1">
                      <p className="text-slate-400 text-xs font-bold uppercase tracking-wider">
                        {result.inputValue1} {result.inputUnit1} {operator === 'ADD' ? '+' : operator === 'SUBTRACT' ? '-' : '/'} {result.inputValue2} {result.inputUnit2}
                      </p>
                      <h2 className="text-5xl font-black bg-gradient-to-r from-indigo-200 via-slate-100 to-cyan-200 bg-clip-text text-transparent break-words p-2">
                        {result.resultValue % 1 === 0 ? result.resultValue : result.resultValue.toFixed(4)}
                      </h2>
                      <p className="text-slate-300 font-extrabold text-lg uppercase tracking-wide">{result.resultUnit}</p>
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* 4. Empty Idle state */}
            {!loading && !error && !result && (
              <div className="text-center space-y-3 relative z-10 py-12">
                <div className="h-12 w-12 bg-slate-900 border border-slate-800 rounded-2xl flex items-center justify-center mx-auto text-slate-500">
                  <RefreshCw className="h-6 w-6 animate-spin duration-[4000ms]" />
                </div>
                <h4 className="font-bold text-slate-400 text-sm">Awaiting Instruction</h4>
                <p className="text-slate-500 text-xs max-w-[220px] mx-auto leading-relaxed">Fill in the quantity coordinates on the left and compute the solution.</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
