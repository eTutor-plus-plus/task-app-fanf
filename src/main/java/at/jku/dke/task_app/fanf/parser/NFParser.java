// Generated from NF.g4 by ANTLR 4.13.1

package at.jku.dke.task_app.fanf.parser;


import at.jku.dke.task_app.fanf.evaluation.model.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class NFParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9,
		T__9=10, T__10=11, T__11=12, Integer=13, AlphaNumericChain=14, Digit=15,
		Letter=16, WhiteSpace=17;
	public static final int
		RULE_relationSetSubmission = 0, RULE_relation = 1, RULE_relationId = 2,
		RULE_keySetSubmission = 3, RULE_keySet = 4, RULE_key = 5, RULE_normalFormSubmission = 6,
		RULE_normalFormViolationSet = 7, RULE_normalFormViolation = 8, RULE_normalFormSpecification = 9,
		RULE_normalForm = 10, RULE_functionalDependencySetSubmission = 11, RULE_functionalDependencySet = 12,
		RULE_functionalDependency = 13, RULE_attributeSetSubmission = 14, RULE_attributeSet = 15,
		RULE_attribute = 16;
	private static String[] makeRuleNames() {
		return new String[] {
			"relationSetSubmission", "relation", "relationId", "keySetSubmission",
			"keySet", "key", "normalFormSubmission", "normalFormViolationSet", "normalFormViolation",
			"normalFormSpecification", "normalForm", "functionalDependencySetSubmission",
			"functionalDependencySet", "functionalDependency", "attributeSetSubmission",
			"attributeSet", "attribute"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "':'", "'('", "')'", "'->'", "'#'", "'.'", "'1NF'", "'2NF'",
			"'3NF'", "'BCNF'", "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null,
			null, "Integer", "AlphaNumericChain", "Digit", "Letter", "WhiteSpace"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "NF.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public NFParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RelationSetSubmissionContext extends ParserRuleContext {
		public Set<IdentifiedRelation> relations;
		public RelationContext relation;
		public List<RelationContext> relation() {
			return getRuleContexts(RelationContext.class);
		}
		public RelationContext relation(int i) {
			return getRuleContext(RelationContext.class,i);
		}
		public TerminalNode EOF() { return getToken(NFParser.EOF, 0); }
		public RelationSetSubmissionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationSetSubmission; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterRelationSetSubmission(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitRelationSetSubmission(this);
		}
	}

	public final RelationSetSubmissionContext relationSetSubmission() throws RecognitionException {
		RelationSetSubmissionContext _localctx = new RelationSetSubmissionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_relationSetSubmission);

		        ((RelationSetSubmissionContext)_localctx).relations =  new HashSet<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			((RelationSetSubmissionContext)_localctx).relation = relation();
			_localctx.relations.add(((RelationSetSubmissionContext)_localctx).relation.parsedRelation);
			setState(42);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(36);
				match(T__0);
				setState(37);
				((RelationSetSubmissionContext)_localctx).relation = relation();
				_localctx.relations.add(((RelationSetSubmissionContext)_localctx).relation.parsedRelation);
				}
				}
				setState(44);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(45);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RelationContext extends ParserRuleContext {
		public IdentifiedRelation parsedRelation;
		public RelationIdContext relationId;
		public AttributeSetContext attributeSet;
		public FunctionalDependencySetContext functionalDependencySet;
		public KeySetContext keySet;
		public RelationIdContext relationId() {
			return getRuleContext(RelationIdContext.class,0);
		}
		public AttributeSetContext attributeSet() {
			return getRuleContext(AttributeSetContext.class,0);
		}
		public KeySetContext keySet() {
			return getRuleContext(KeySetContext.class,0);
		}
		public FunctionalDependencySetContext functionalDependencySet() {
			return getRuleContext(FunctionalDependencySetContext.class,0);
		}
		public RelationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterRelation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitRelation(this);
		}
	}

	public final RelationContext relation() throws RecognitionException {
		RelationContext _localctx = new RelationContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_relation);

		        ((RelationContext)_localctx).parsedRelation =  new IdentifiedRelation();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
			((RelationContext)_localctx).relationId = relationId();
			try {_localctx.parsedRelation.setID(((RelationContext)_localctx).relationId.idString);} catch(Exception e) { e.printStackTrace();}
			setState(49);
			match(T__1);
			setState(50);
			match(T__2);
			setState(51);
			((RelationContext)_localctx).attributeSet = attributeSet();
			_localctx.parsedRelation.setAttributes(((RelationContext)_localctx).attributeSet.attributes);
			setState(53);
			match(T__3);
			setState(54);
			match(T__4);
			setState(55);
			match(T__2);
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AlphaNumericChain) {
				{
				setState(56);
				((RelationContext)_localctx).functionalDependencySet = functionalDependencySet();
				_localctx.parsedRelation.setFunctionalDependencies(((RelationContext)_localctx).functionalDependencySet.functionalDependencies);
				}
			}

			setState(61);
			match(T__3);
			setState(62);
			match(T__5);
			setState(63);
			match(T__2);
			setState(64);
			((RelationContext)_localctx).keySet = keySet();
			_localctx.parsedRelation.setMinimalKeys(((RelationContext)_localctx).keySet.keys);
			setState(66);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RelationIdContext extends ParserRuleContext {
		public String idString;
		public Token AlphaNumericChain;
		public TerminalNode AlphaNumericChain() { return getToken(NFParser.AlphaNumericChain, 0); }
		public RelationIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationId; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterRelationId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitRelationId(this);
		}
	}

	public final RelationIdContext relationId() throws RecognitionException {
		RelationIdContext _localctx = new RelationIdContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_relationId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			((RelationIdContext)_localctx).AlphaNumericChain = match(AlphaNumericChain);
			((RelationIdContext)_localctx).idString =  (((RelationIdContext)_localctx).AlphaNumericChain!=null?((RelationIdContext)_localctx).AlphaNumericChain.getText():null);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class KeySetSubmissionContext extends ParserRuleContext {
		public Set<Key> keys;
		public KeySetContext keySet;
		public KeySetContext keySet() {
			return getRuleContext(KeySetContext.class,0);
		}
		public TerminalNode EOF() { return getToken(NFParser.EOF, 0); }
		public KeySetSubmissionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keySetSubmission; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterKeySetSubmission(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitKeySetSubmission(this);
		}
	}

	public final KeySetSubmissionContext keySetSubmission() throws RecognitionException {
		KeySetSubmissionContext _localctx = new KeySetSubmissionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_keySetSubmission);

		            ((KeySetSubmissionContext)_localctx).keys =  new HashSet<>();

		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			((KeySetSubmissionContext)_localctx).keySet = keySet();
			_localctx.keys.addAll(((KeySetSubmissionContext)_localctx).keySet.keys);
			setState(73);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class KeySetContext extends ParserRuleContext {
		public Set<Key> keys;
		public KeyContext key;
		public List<KeyContext> key() {
			return getRuleContexts(KeyContext.class);
		}
		public KeyContext key(int i) {
			return getRuleContext(KeyContext.class,i);
		}
		public KeySetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keySet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterKeySet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitKeySet(this);
		}
	}

	public final KeySetContext keySet() throws RecognitionException {
		KeySetContext _localctx = new KeySetContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_keySet);

		        ((KeySetContext)_localctx).keys =  new HashSet<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			((KeySetContext)_localctx).key = key();
			_localctx.keys.add(((KeySetContext)_localctx).key.keyObject);
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(77);
				match(T__0);
				setState(78);
				((KeySetContext)_localctx).key = key();
				_localctx.keys.add(((KeySetContext)_localctx).key.keyObject);
				}
				}
				setState(85);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class KeyContext extends ParserRuleContext {
		public Key keyObject;
		public AttributeSetContext attributeSet;
		public AttributeSetContext attributeSet() {
			return getRuleContext(AttributeSetContext.class,0);
		}
		public KeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_key; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitKey(this);
		}
	}

	public final KeyContext key() throws RecognitionException {
		KeyContext _localctx = new KeyContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_key);

		        ((KeyContext)_localctx).keyObject =  new Key();

		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			((KeyContext)_localctx).attributeSet = attributeSet();
			_localctx.keyObject.addAllAttributes(((KeyContext)_localctx).attributeSet.attributes);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NormalFormSubmissionContext extends ParserRuleContext {
		public NormalformDeterminationSubmission submission;
		public NormalFormContext normalForm;
		public NormalFormViolationSetContext normalFormViolationSet;
		public NormalFormContext normalForm() {
			return getRuleContext(NormalFormContext.class,0);
		}
		public TerminalNode EOF() { return getToken(NFParser.EOF, 0); }
		public NormalFormViolationSetContext normalFormViolationSet() {
			return getRuleContext(NormalFormViolationSetContext.class,0);
		}
		public NormalFormSubmissionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normalFormSubmission; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterNormalFormSubmission(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitNormalFormSubmission(this);
		}
	}

	public final NormalFormSubmissionContext normalFormSubmission() throws RecognitionException {
		NormalFormSubmissionContext _localctx = new NormalFormSubmissionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_normalFormSubmission);

		        ((NormalFormSubmissionContext)_localctx).submission =  new NormalformDeterminationSubmission();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			((NormalFormSubmissionContext)_localctx).normalForm = normalForm();
			_localctx.submission.setOverallLevel(((NormalFormSubmissionContext)_localctx).normalForm.level);
			setState(91);
			match(T__6);
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AlphaNumericChain) {
				{
				setState(92);
				((NormalFormSubmissionContext)_localctx).normalFormViolationSet = normalFormViolationSet();
				_localctx.submission.setNormalformViolations(((NormalFormSubmissionContext)_localctx).normalFormViolationSet.violations);
				}
			}

			setState(97);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NormalFormViolationSetContext extends ParserRuleContext {
		public Map<FunctionalDependency, NormalformLevel> violations;
		public NormalFormViolationContext normalFormViolation;
		public List<NormalFormViolationContext> normalFormViolation() {
			return getRuleContexts(NormalFormViolationContext.class);
		}
		public NormalFormViolationContext normalFormViolation(int i) {
			return getRuleContext(NormalFormViolationContext.class,i);
		}
		public NormalFormViolationSetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normalFormViolationSet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterNormalFormViolationSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitNormalFormViolationSet(this);
		}
	}

	public final NormalFormViolationSetContext normalFormViolationSet() throws RecognitionException {
		NormalFormViolationSetContext _localctx = new NormalFormViolationSetContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_normalFormViolationSet);

		        ((NormalFormViolationSetContext)_localctx).violations =  new HashMap<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			((NormalFormViolationSetContext)_localctx).normalFormViolation = normalFormViolation();
			_localctx.violations.put(((NormalFormViolationSetContext)_localctx).normalFormViolation.funcDependency, ((NormalFormViolationSetContext)_localctx).normalFormViolation.level);
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(101);
				match(T__0);
				setState(102);
				((NormalFormViolationSetContext)_localctx).normalFormViolation = normalFormViolation();
				_localctx.violations.put(((NormalFormViolationSetContext)_localctx).normalFormViolation.funcDependency, ((NormalFormViolationSetContext)_localctx).normalFormViolation.level);
				}
				}
				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NormalFormViolationContext extends ParserRuleContext {
		public FunctionalDependency funcDependency;
		public NormalformLevel level;
		public FunctionalDependencyContext functionalDependency;
		public NormalFormContext normalForm;
		public FunctionalDependencyContext functionalDependency() {
			return getRuleContext(FunctionalDependencyContext.class,0);
		}
		public NormalFormContext normalForm() {
			return getRuleContext(NormalFormContext.class,0);
		}
		public NormalFormViolationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normalFormViolation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterNormalFormViolation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitNormalFormViolation(this);
		}
	}

	public final NormalFormViolationContext normalFormViolation() throws RecognitionException {
		NormalFormViolationContext _localctx = new NormalFormViolationContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_normalFormViolation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			((NormalFormViolationContext)_localctx).functionalDependency = functionalDependency();
			((NormalFormViolationContext)_localctx).funcDependency =  ((NormalFormViolationContext)_localctx).functionalDependency.fdObject;
			setState(112);
			match(T__1);
			setState(113);
			((NormalFormViolationContext)_localctx).normalForm = normalForm();
			((NormalFormViolationContext)_localctx).level =  ((NormalFormViolationContext)_localctx).normalForm.level;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NormalFormSpecificationContext extends ParserRuleContext {
		public NormalformLevel level;
		public NormalFormContext normalForm;
		public NormalFormContext normalForm() {
			return getRuleContext(NormalFormContext.class,0);
		}
		public TerminalNode EOF() { return getToken(NFParser.EOF, 0); }
		public NormalFormSpecificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normalFormSpecification; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterNormalFormSpecification(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitNormalFormSpecification(this);
		}
	}

	public final NormalFormSpecificationContext normalFormSpecification() throws RecognitionException {
		NormalFormSpecificationContext _localctx = new NormalFormSpecificationContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_normalFormSpecification);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			((NormalFormSpecificationContext)_localctx).normalForm = normalForm();
			((NormalFormSpecificationContext)_localctx).level =  ((NormalFormSpecificationContext)_localctx).normalForm.level;
			setState(118);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NormalFormContext extends ParserRuleContext {
		public NormalformLevel level;
		public NormalFormContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normalForm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterNormalForm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitNormalForm(this);
		}
	}

	public final NormalFormContext normalForm() throws RecognitionException {
		NormalFormContext _localctx = new NormalFormContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_normalForm);
		try {
			setState(128);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				enterOuterAlt(_localctx, 1);
				{
				setState(120);
				match(T__7);
				((NormalFormContext)_localctx).level =  NormalformLevel.FIRST;
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				setState(122);
				match(T__8);
				((NormalFormContext)_localctx).level =  NormalformLevel.SECOND;
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 3);
				{
				setState(124);
				match(T__9);
				((NormalFormContext)_localctx).level =  NormalformLevel.THIRD;
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 4);
				{
				setState(126);
				match(T__10);
				((NormalFormContext)_localctx).level =  NormalformLevel.BOYCE_CODD;
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionalDependencySetSubmissionContext extends ParserRuleContext {
		public Set<FunctionalDependency> functionalDependencies;
		public FunctionalDependencySetContext functionalDependencySet;
		public FunctionalDependencySetContext functionalDependencySet() {
			return getRuleContext(FunctionalDependencySetContext.class,0);
		}
		public TerminalNode EOF() { return getToken(NFParser.EOF, 0); }
		public FunctionalDependencySetSubmissionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionalDependencySetSubmission; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterFunctionalDependencySetSubmission(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitFunctionalDependencySetSubmission(this);
		}
	}

	public final FunctionalDependencySetSubmissionContext functionalDependencySetSubmission() throws RecognitionException {
		FunctionalDependencySetSubmissionContext _localctx = new FunctionalDependencySetSubmissionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_functionalDependencySetSubmission);

		        ((FunctionalDependencySetSubmissionContext)_localctx).functionalDependencies =  new HashSet<>();

		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			((FunctionalDependencySetSubmissionContext)_localctx).functionalDependencySet = functionalDependencySet();
			_localctx.functionalDependencies.addAll(((FunctionalDependencySetSubmissionContext)_localctx).functionalDependencySet.functionalDependencies);
			setState(132);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionalDependencySetContext extends ParserRuleContext {
		public Set<FunctionalDependency> functionalDependencies;
		public FunctionalDependencyContext functionalDependency;
		public List<FunctionalDependencyContext> functionalDependency() {
			return getRuleContexts(FunctionalDependencyContext.class);
		}
		public FunctionalDependencyContext functionalDependency(int i) {
			return getRuleContext(FunctionalDependencyContext.class,i);
		}
		public FunctionalDependencySetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionalDependencySet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterFunctionalDependencySet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitFunctionalDependencySet(this);
		}
	}

	public final FunctionalDependencySetContext functionalDependencySet() throws RecognitionException {
		FunctionalDependencySetContext _localctx = new FunctionalDependencySetContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_functionalDependencySet);

		        ((FunctionalDependencySetContext)_localctx).functionalDependencies =  new HashSet<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			((FunctionalDependencySetContext)_localctx).functionalDependency = functionalDependency();
			_localctx.functionalDependencies.add(((FunctionalDependencySetContext)_localctx).functionalDependency.fdObject);
			setState(142);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(136);
				match(T__0);
				setState(137);
				((FunctionalDependencySetContext)_localctx).functionalDependency = functionalDependency();
				_localctx.functionalDependencies.add(((FunctionalDependencySetContext)_localctx).functionalDependency.fdObject);
				}
				}
				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionalDependencyContext extends ParserRuleContext {
		public FunctionalDependency fdObject;
		public AttributeSetContext attributeSet;
		public List<AttributeSetContext> attributeSet() {
			return getRuleContexts(AttributeSetContext.class);
		}
		public AttributeSetContext attributeSet(int i) {
			return getRuleContext(AttributeSetContext.class,i);
		}
		public FunctionalDependencyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionalDependency; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterFunctionalDependency(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitFunctionalDependency(this);
		}
	}

	public final FunctionalDependencyContext functionalDependency() throws RecognitionException {
		FunctionalDependencyContext _localctx = new FunctionalDependencyContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_functionalDependency);

		        ((FunctionalDependencyContext)_localctx).fdObject =  new FunctionalDependency();

		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(145);
			((FunctionalDependencyContext)_localctx).attributeSet = attributeSet();
			_localctx.fdObject.setLhsAttributes(((FunctionalDependencyContext)_localctx).attributeSet.attributes);
			setState(147);
			match(T__4);
			setState(148);
			((FunctionalDependencyContext)_localctx).attributeSet = attributeSet();
			_localctx.fdObject.setRhsAttributes(((FunctionalDependencyContext)_localctx).attributeSet.attributes);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AttributeSetSubmissionContext extends ParserRuleContext {
		public Set<String> attributes;
		public AttributeSetContext attributeSet;
		public AttributeSetContext attributeSet() {
			return getRuleContext(AttributeSetContext.class,0);
		}
		public TerminalNode EOF() { return getToken(NFParser.EOF, 0); }
		public AttributeSetSubmissionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeSetSubmission; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterAttributeSetSubmission(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitAttributeSetSubmission(this);
		}
	}

	public final AttributeSetSubmissionContext attributeSetSubmission() throws RecognitionException {
		AttributeSetSubmissionContext _localctx = new AttributeSetSubmissionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_attributeSetSubmission);

		        ((AttributeSetSubmissionContext)_localctx).attributes =  new HashSet<>();

		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			((AttributeSetSubmissionContext)_localctx).attributeSet = attributeSet();
			_localctx.attributes.addAll(((AttributeSetSubmissionContext)_localctx).attributeSet.attributes);
			setState(153);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AttributeSetContext extends ParserRuleContext {
		public Set<String> attributes;
		public AttributeContext attribute;
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public AttributeSetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeSet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterAttributeSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitAttributeSet(this);
		}
	}

	public final AttributeSetContext attributeSet() throws RecognitionException {
		AttributeSetContext _localctx = new AttributeSetContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_attributeSet);

		        ((AttributeSetContext)_localctx).attributes =  new HashSet<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			((AttributeSetContext)_localctx).attribute = attribute();
			_localctx.attributes.add((((AttributeSetContext)_localctx).attribute!=null?_input.getText(((AttributeSetContext)_localctx).attribute.start,((AttributeSetContext)_localctx).attribute.stop):null));
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(157);
				match(T__11);
				setState(158);
				((AttributeSetContext)_localctx).attribute = attribute();
				_localctx.attributes.add((((AttributeSetContext)_localctx).attribute!=null?_input.getText(((AttributeSetContext)_localctx).attribute.start,((AttributeSetContext)_localctx).attribute.stop):null));
				}
				}
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AttributeContext extends ParserRuleContext {
		public TerminalNode AlphaNumericChain() { return getToken(NFParser.AlphaNumericChain, 0); }
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NFListener ) ((NFListener)listener).exitAttribute(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_attribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(AlphaNumericChain);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0011\u00a9\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0005\u0000)\b\u0000\n\u0000\f\u0000,\t"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001<\b\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0005\u0004R\b\u0004\n\u0004\f\u0004U\t\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0003\u0006`\b\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005"+
		"\u0007j\b\u0007\n\u0007\f\u0007m\t\u0007\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u0081\b\n\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0005\f\u008d\b\f\n\f\f\f\u0090\t\f\u0001\r\u0001\r\u0001\r"+
		"\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0005\u000f\u00a2\b\u000f\n\u000f\f\u000f\u00a5\t\u000f\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0000\u0000\u0011\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \u0000\u0000\u00a1\u0000"+
		"\"\u0001\u0000\u0000\u0000\u0002/\u0001\u0000\u0000\u0000\u0004D\u0001"+
		"\u0000\u0000\u0000\u0006G\u0001\u0000\u0000\u0000\bK\u0001\u0000\u0000"+
		"\u0000\nV\u0001\u0000\u0000\u0000\fY\u0001\u0000\u0000\u0000\u000ec\u0001"+
		"\u0000\u0000\u0000\u0010n\u0001\u0000\u0000\u0000\u0012t\u0001\u0000\u0000"+
		"\u0000\u0014\u0080\u0001\u0000\u0000\u0000\u0016\u0082\u0001\u0000\u0000"+
		"\u0000\u0018\u0086\u0001\u0000\u0000\u0000\u001a\u0091\u0001\u0000\u0000"+
		"\u0000\u001c\u0097\u0001\u0000\u0000\u0000\u001e\u009b\u0001\u0000\u0000"+
		"\u0000 \u00a6\u0001\u0000\u0000\u0000\"#\u0003\u0002\u0001\u0000#*\u0006"+
		"\u0000\uffff\uffff\u0000$%\u0005\u0001\u0000\u0000%&\u0003\u0002\u0001"+
		"\u0000&\'\u0006\u0000\uffff\uffff\u0000\')\u0001\u0000\u0000\u0000($\u0001"+
		"\u0000\u0000\u0000),\u0001\u0000\u0000\u0000*(\u0001\u0000\u0000\u0000"+
		"*+\u0001\u0000\u0000\u0000+-\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000"+
		"\u0000-.\u0005\u0000\u0000\u0001.\u0001\u0001\u0000\u0000\u0000/0\u0003"+
		"\u0004\u0002\u000001\u0006\u0001\uffff\uffff\u000012\u0005\u0002\u0000"+
		"\u000023\u0005\u0003\u0000\u000034\u0003\u001e\u000f\u000045\u0006\u0001"+
		"\uffff\uffff\u000056\u0005\u0004\u0000\u000067\u0005\u0005\u0000\u0000"+
		"7;\u0005\u0003\u0000\u000089\u0003\u0018\f\u00009:\u0006\u0001\uffff\uffff"+
		"\u0000:<\u0001\u0000\u0000\u0000;8\u0001\u0000\u0000\u0000;<\u0001\u0000"+
		"\u0000\u0000<=\u0001\u0000\u0000\u0000=>\u0005\u0004\u0000\u0000>?\u0005"+
		"\u0006\u0000\u0000?@\u0005\u0003\u0000\u0000@A\u0003\b\u0004\u0000AB\u0006"+
		"\u0001\uffff\uffff\u0000BC\u0005\u0004\u0000\u0000C\u0003\u0001\u0000"+
		"\u0000\u0000DE\u0005\u000e\u0000\u0000EF\u0006\u0002\uffff\uffff\u0000"+
		"F\u0005\u0001\u0000\u0000\u0000GH\u0003\b\u0004\u0000HI\u0006\u0003\uffff"+
		"\uffff\u0000IJ\u0005\u0000\u0000\u0001J\u0007\u0001\u0000\u0000\u0000"+
		"KL\u0003\n\u0005\u0000LS\u0006\u0004\uffff\uffff\u0000MN\u0005\u0001\u0000"+
		"\u0000NO\u0003\n\u0005\u0000OP\u0006\u0004\uffff\uffff\u0000PR\u0001\u0000"+
		"\u0000\u0000QM\u0001\u0000\u0000\u0000RU\u0001\u0000\u0000\u0000SQ\u0001"+
		"\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000T\t\u0001\u0000\u0000\u0000"+
		"US\u0001\u0000\u0000\u0000VW\u0003\u001e\u000f\u0000WX\u0006\u0005\uffff"+
		"\uffff\u0000X\u000b\u0001\u0000\u0000\u0000YZ\u0003\u0014\n\u0000Z[\u0006"+
		"\u0006\uffff\uffff\u0000[_\u0005\u0007\u0000\u0000\\]\u0003\u000e\u0007"+
		"\u0000]^\u0006\u0006\uffff\uffff\u0000^`\u0001\u0000\u0000\u0000_\\\u0001"+
		"\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`a\u0001\u0000\u0000\u0000"+
		"ab\u0005\u0000\u0000\u0001b\r\u0001\u0000\u0000\u0000cd\u0003\u0010\b"+
		"\u0000dk\u0006\u0007\uffff\uffff\u0000ef\u0005\u0001\u0000\u0000fg\u0003"+
		"\u0010\b\u0000gh\u0006\u0007\uffff\uffff\u0000hj\u0001\u0000\u0000\u0000"+
		"ie\u0001\u0000\u0000\u0000jm\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000"+
		"\u0000kl\u0001\u0000\u0000\u0000l\u000f\u0001\u0000\u0000\u0000mk\u0001"+
		"\u0000\u0000\u0000no\u0003\u001a\r\u0000op\u0006\b\uffff\uffff\u0000p"+
		"q\u0005\u0002\u0000\u0000qr\u0003\u0014\n\u0000rs\u0006\b\uffff\uffff"+
		"\u0000s\u0011\u0001\u0000\u0000\u0000tu\u0003\u0014\n\u0000uv\u0006\t"+
		"\uffff\uffff\u0000vw\u0005\u0000\u0000\u0001w\u0013\u0001\u0000\u0000"+
		"\u0000xy\u0005\b\u0000\u0000y\u0081\u0006\n\uffff\uffff\u0000z{\u0005"+
		"\t\u0000\u0000{\u0081\u0006\n\uffff\uffff\u0000|}\u0005\n\u0000\u0000"+
		"}\u0081\u0006\n\uffff\uffff\u0000~\u007f\u0005\u000b\u0000\u0000\u007f"+
		"\u0081\u0006\n\uffff\uffff\u0000\u0080x\u0001\u0000\u0000\u0000\u0080"+
		"z\u0001\u0000\u0000\u0000\u0080|\u0001\u0000\u0000\u0000\u0080~\u0001"+
		"\u0000\u0000\u0000\u0081\u0015\u0001\u0000\u0000\u0000\u0082\u0083\u0003"+
		"\u0018\f\u0000\u0083\u0084\u0006\u000b\uffff\uffff\u0000\u0084\u0085\u0005"+
		"\u0000\u0000\u0001\u0085\u0017\u0001\u0000\u0000\u0000\u0086\u0087\u0003"+
		"\u001a\r\u0000\u0087\u008e\u0006\f\uffff\uffff\u0000\u0088\u0089\u0005"+
		"\u0001\u0000\u0000\u0089\u008a\u0003\u001a\r\u0000\u008a\u008b\u0006\f"+
		"\uffff\uffff\u0000\u008b\u008d\u0001\u0000\u0000\u0000\u008c\u0088\u0001"+
		"\u0000\u0000\u0000\u008d\u0090\u0001\u0000\u0000\u0000\u008e\u008c\u0001"+
		"\u0000\u0000\u0000\u008e\u008f\u0001\u0000\u0000\u0000\u008f\u0019\u0001"+
		"\u0000\u0000\u0000\u0090\u008e\u0001\u0000\u0000\u0000\u0091\u0092\u0003"+
		"\u001e\u000f\u0000\u0092\u0093\u0006\r\uffff\uffff\u0000\u0093\u0094\u0005"+
		"\u0005\u0000\u0000\u0094\u0095\u0003\u001e\u000f\u0000\u0095\u0096\u0006"+
		"\r\uffff\uffff\u0000\u0096\u001b\u0001\u0000\u0000\u0000\u0097\u0098\u0003"+
		"\u001e\u000f\u0000\u0098\u0099\u0006\u000e\uffff\uffff\u0000\u0099\u009a"+
		"\u0005\u0000\u0000\u0001\u009a\u001d\u0001\u0000\u0000\u0000\u009b\u009c"+
		"\u0003 \u0010\u0000\u009c\u00a3\u0006\u000f\uffff\uffff\u0000\u009d\u009e"+
		"\u0005\f\u0000\u0000\u009e\u009f\u0003 \u0010\u0000\u009f\u00a0\u0006"+
		"\u000f\uffff\uffff\u0000\u00a0\u00a2\u0001\u0000\u0000\u0000\u00a1\u009d"+
		"\u0001\u0000\u0000\u0000\u00a2\u00a5\u0001\u0000\u0000\u0000\u00a3\u00a1"+
		"\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4\u001f"+
		"\u0001\u0000\u0000\u0000\u00a5\u00a3\u0001\u0000\u0000\u0000\u00a6\u00a7"+
		"\u0005\u000e\u0000\u0000\u00a7!\u0001\u0000\u0000\u0000\b*;S_k\u0080\u008e"+
		"\u00a3";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
